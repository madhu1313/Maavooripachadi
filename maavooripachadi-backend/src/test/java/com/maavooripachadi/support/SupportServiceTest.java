package com.maavooripachadi.support;

import com.maavooripachadi.support.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportServiceTest {

    @Mock private SupportTicketRepository ticketRepository;
    @Mock private TicketMessageRepository messageRepository;
    @Mock private TicketAttachmentRepository attachmentRepository;
    @Mock private TicketEventRepository eventRepository;
    @Mock private SlaService slaService;
    @Mock private CannedResponseRepository cannedResponseRepository;
    @Mock private CsatSurveyRepository csatSurveyRepository;

    private SupportService service;

    @BeforeEach
    void setUp() {
        service = new SupportService(ticketRepository, messageRepository, attachmentRepository, eventRepository, slaService, cannedResponseRepository, csatSurveyRepository);
    }

    @Test
    void openPersistsTicketItemsAndLogsEvent() {
        CreateTicketRequest request = new CreateTicketRequest();
        request.setSubject("Need help");
        request.setDescription("My order is delayed");
        request.setRequesterEmail("user@example.com");
        request.setRequesterName("User");

        when(ticketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 11L);
            return saved;
        });

        SupportTicket ticket = service.open(request);

        verify(slaService).applyDefaults(ticket);
        verify(ticketRepository).save(ticket);
        verify(eventRepository).save(any(TicketEvent.class));
        assertThat(ticket.getSubject()).isEqualTo("Need help");
    }

    @Test
    void addMessageUpdatesTicketStatusForPublicMessage() {
        SupportTicket ticket = new SupportTicket();
        ticket.setStatus(TicketStatus.OPEN);
        when(ticketRepository.findByTicketNo("ST-1")).thenReturn(Optional.of(ticket));
        when(messageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        AddMessageRequest request = new AddMessageRequest();
        request.setTicketNo("ST-1");
        request.setAuthor("agent");
        request.setBody("Thanks for reaching out");
        request.setVisibility(MessageVisibility.PUBLIC);

        TicketMessage message = service.addMessage(request);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.PENDING_AGENT);
        assertThat(message.getAuthor()).isEqualTo("agent");
        verify(eventRepository).save(any(TicketEvent.class));
    }

    @Test
    void viewReturnsTicketAndMessages() {
        SupportTicket ticket = new SupportTicket();
        ReflectionTestUtils.setField(ticket, "id", 25L);
        when(ticketRepository.findByTicketNo("ST-2")).thenReturn(Optional.of(ticket));
        when(messageRepository.findByTicketIdOrderByCreatedAtAsc(25L)).thenReturn(List.of());

        Map<String, Object> view = service.view("ST-2");

        assertThat(view).containsEntry("ticket", ticket);
        assertThat((List<?>) view.get("messages")).isEmpty();
    }

    @Test
    void replyUsesAddMessageAndReturnsMessageReference() {
        AddMessageRequest captured = new AddMessageRequest();
        TicketMessage message = new TicketMessage();
        ReflectionTestUtils.setField(message, "id", 55L);

        SupportService spyService = spy(service);
        doReturn(message).when(spyService).addMessage(any(AddMessageRequest.class));

        Map<String, Object> reply = spyService.reply("ST-3", "agent", "Hello");

        assertThat(reply).containsEntry("ticketNo", "ST-3");
        assertThat(reply.get("messageId")).isEqualTo(55L);
    }

    @Test
    void assignDelegatesToRepository() {
        SupportTicket ticket = new SupportTicket();
        when(ticketRepository.findByTicketNo("ST-4")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        AssignAgentRequest request = new AssignAgentRequest();
        request.setTicketNo("ST-4");
        request.setAgent("agent1");

        SupportTicket result = service.assign(request);

        assertThat(result.getAssignee()).isEqualTo("agent1");
        verify(eventRepository).save(any(TicketEvent.class));
    }

    @Test
    void updateStatusSetsClosedAtWhenResolved() {
        SupportTicket ticket = new SupportTicket();
        when(ticketRepository.findByTicketNo("ST-5")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setTicketNo("ST-5");
        request.setStatus(TicketStatus.RESOLVED);
        request.setNote("Issue fixed");

        SupportTicket result = service.updateStatus(request);

        assertThat(result.getStatus()).isEqualTo(TicketStatus.RESOLVED);
        assertThat(result.getClosedAt()).isNotNull();
        verify(eventRepository).save(any(TicketEvent.class));
    }

    @Test
    void addTagAddsTagAndLogsEvent() {
        SupportTicket ticket = new SupportTicket();
        when(ticketRepository.findByTicketNo("ST-6")).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        AddTagRequest request = new AddTagRequest();
        request.setTicketNo("ST-6");
        request.setTag("vip");

        SupportTicket result = service.addTag(request);

        assertThat(result.getTags()).contains("vip");
        verify(eventRepository).save(any(TicketEvent.class));
    }

    @Test
    void submitCsatPersistsSurvey() {
        SupportTicket ticket = new SupportTicket();
        when(ticketRepository.findByTicketNo("ST-7")).thenReturn(Optional.of(ticket));
        CsatSurvey savedSurvey = new CsatSurvey();
        when(csatSurveyRepository.save(any(CsatSurvey.class))).thenReturn(savedSurvey);

        CsatSubmitRequest request = new CsatSubmitRequest();
        request.setTicketNo("ST-7");
        request.setRating(5);
        request.setComment("Great service");

        CsatSurvey result = service.submitCsat(request);

        assertThat(result).isSameAs(savedSurvey);
    }
}
