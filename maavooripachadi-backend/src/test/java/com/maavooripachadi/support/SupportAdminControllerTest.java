package com.maavooripachadi.support;

import com.maavooripachadi.support.dto.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportAdminControllerTest {

    @Mock private SupportService supportService;
    @Mock private CannedResponseService cannedResponseService;
    @Mock private SupportTicketRepository ticketRepository;
    @Mock private TicketMessageRepository messageRepository;

    private SupportAdminController controller;

    @BeforeEach
    void setUp() {
        controller = new SupportAdminController(supportService, cannedResponseService, ticketRepository, messageRepository);
    }

    @Test
    void listReturnsTicketsFromService() {
        Page<SupportTicket> page = new PageImpl<>(List.of(new SupportTicket()));
        when(supportService.list(TicketStatus.OPEN, 0, 20)).thenReturn(page);

        assertThat(controller.list(TicketStatus.OPEN, 0, 20)).isSameAs(page);
        verify(supportService).list(TicketStatus.OPEN, 0, 20);
    }

    @Test
    void assignDelegatesToService() {
        AssignAgentRequest request = new AssignAgentRequest();
        SupportTicket ticket = new SupportTicket();
        when(supportService.assign(request)).thenReturn(ticket);

        assertThat(controller.assign(request)).isSameAs(ticket);
    }

    @Test
    void statusDelegatesToService() {
        UpdateStatusRequest request = new UpdateStatusRequest();
        SupportTicket ticket = new SupportTicket();
        when(supportService.updateStatus(request)).thenReturn(ticket);

        assertThat(controller.status(request)).isSameAs(ticket);
    }

    @Test
    void tagDelegatesToService() {
        AddTagRequest request = new AddTagRequest();
        SupportTicket ticket = new SupportTicket();
        when(supportService.addTag(request)).thenReturn(ticket);

        assertThat(controller.tag(request)).isSameAs(ticket);
    }

    @Test
    void cannedDelegatesToCannedService() {
        CannedUpsertRequest request = new CannedUpsertRequest();
        CannedResponse response = new CannedResponse();
        when(cannedResponseService.upsert(request)).thenReturn(response);

        assertThat(controller.upsert(request)).isSameAs(response);
    }

    @Test
    void messagesReturnsMessagesForTicket() {
        SupportTicket ticket = new SupportTicket();
        ReflectionTestUtils.setField(ticket, "id", 42L);
        when(ticketRepository.findByTicketNo("ST-42")).thenReturn(Optional.of(ticket));
        TicketMessage message = new TicketMessage();
        when(messageRepository.findByTicketIdOrderByCreatedAtAsc(42L)).thenReturn(List.of(message));

        assertThat(controller.messages("ST-42")).containsExactly(message);
    }
}
