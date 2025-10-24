package com.maavooripachadi.support;

import com.maavooripachadi.support.dto.AddMessageRequest;
import com.maavooripachadi.support.dto.CreateTicketRequest;
import com.maavooripachadi.support.dto.CsatSubmitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportPublicControllerTest {

    @Mock private SupportService supportService;

    private SupportPublicController controller;

    @BeforeEach
    void setUp() {
        controller = new SupportPublicController(supportService);
    }

    @Test
    void openDelegatesToService() {
        CreateTicketRequest request = new CreateTicketRequest();
        SupportTicket ticket = new SupportTicket();
        when(supportService.open(request)).thenReturn(ticket);

        assertThat(controller.open(request)).isSameAs(ticket);
        verify(supportService).open(request);
    }

    @Test
    void messageDelegatesToService() {
        AddMessageRequest request = new AddMessageRequest();
        TicketMessage message = new TicketMessage();
        when(supportService.addMessage(request)).thenReturn(message);

        assertThat(controller.message(request)).isSameAs(message);
        verify(supportService).addMessage(request);
    }

    @Test
    void csatDelegatesToService() {
        CsatSubmitRequest request = new CsatSubmitRequest();
        CsatSurvey survey = new CsatSurvey();
        when(supportService.submitCsat(request)).thenReturn(survey);

        assertThat(controller.csat(request)).isSameAs(survey);
        verify(supportService).submitCsat(request);
    }
}
