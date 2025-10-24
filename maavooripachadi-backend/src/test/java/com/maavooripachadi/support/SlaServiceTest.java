package com.maavooripachadi.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlaServiceTest {

    @Mock private TicketSlaRepository repository;

    private SupportProperties properties;
    private SlaService service;

    @BeforeEach
    void setUp() {
        properties = new SupportProperties();
        properties.setDefaultSlaName("Default");
        service = new SlaService(repository, properties);
    }

    @Test
    void applyDefaultsUsesExistingSla() {
        TicketSla existing = new TicketSla();
        existing.setFirstResponseMins(30);
        existing.setResolveMins(300);
        when(repository.findAll()).thenReturn(List.of(existing));

        SupportTicket ticket = new SupportTicket();
        service.applyDefaults(ticket);

        assertThat(ticket.getFirstResponseDueAt()).isAfter(OffsetDateTime.now().minusMinutes(1));
        assertThat(ticket.getResolveDueAt()).isAfter(ticket.getFirstResponseDueAt());
        verify(repository, never()).save(any());
    }

    @Test
    void applyDefaultsCreatesDefaultWhenMissing() {
        when(repository.findAll()).thenReturn(List.of());
        when(repository.save(any(TicketSla.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SupportTicket ticket = new SupportTicket();
        service.applyDefaults(ticket);

        verify(repository).save(any(TicketSla.class));
        assertThat(ticket.getFirstResponseDueAt()).isNotNull();
        assertThat(ticket.getResolveDueAt()).isNotNull();
    }
}
