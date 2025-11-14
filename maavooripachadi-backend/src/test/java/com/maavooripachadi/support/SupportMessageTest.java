package com.maavooripachadi.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupportMessageTest {

    @Test
    void settersAndGettersWorkForAllFields() {
        SupportMessage message = new SupportMessage();
        message.setTicketId(42L);
        message.setSender("agent@maavooripachadi.com");
        message.setBody("Sample body");
        message.setAttachments("[\"attachment.png\"]");

        assertEquals(42L, message.getTicketId());
        assertEquals("agent@maavooripachadi.com", message.getSender());
        assertEquals("Sample body", message.getBody());
        assertEquals("[\"attachment.png\"]", message.getAttachments());
    }
}
