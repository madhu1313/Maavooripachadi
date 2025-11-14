package com.maavooripachadi.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TicketAttachmentTest {

    @Test
    void storesTicketAndMetadata() {
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNo("ST-1");

        TicketAttachment attachment = new TicketAttachment();
        attachment.setTicket(ticket);
        attachment.setFileName("invoice.pdf");
        attachment.setUrl("https://cdn.example/invoice.pdf");
        attachment.setSizeBytes(2048L);

        assertSame(ticket, attachment.getTicket());
        assertEquals("invoice.pdf", attachment.getFileName());
        assertEquals("https://cdn.example/invoice.pdf", attachment.getUrl());
        assertEquals(2048L, attachment.getSizeBytes());
    }
}
