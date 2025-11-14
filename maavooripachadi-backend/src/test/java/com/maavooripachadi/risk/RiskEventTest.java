package com.maavooripachadi.risk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RiskEventTest {

    @Test
    void settersCoverAllFields() {
        RiskEvent event = new RiskEvent();
        event.setSource(RiskSource.CHECKOUT);
        event.setSubjectId("user-7");
        event.setEmail("user@example.com");
        event.setPhone("+919999999999");
        event.setIp("192.168.0.10");
        event.setDeviceId("device-abc");
        event.setOrderNo("ORD-777");
        event.setAmountPaise(12345);
        event.setCurrency("INR");
        event.setPayloadJson("{\"key\":\"value\"}");

        assertSame(RiskSource.CHECKOUT, event.getSource());
        assertEquals("user-7", event.getSubjectId());
        assertEquals("user@example.com", event.getEmail());
        assertEquals("+919999999999", event.getPhone());
        assertEquals("192.168.0.10", event.getIp());
        assertEquals("device-abc", event.getDeviceId());
        assertEquals("ORD-777", event.getOrderNo());
        assertEquals(12345, event.getAmountPaise());
        assertEquals("INR", event.getCurrency());
        assertEquals("{\"key\":\"value\"}", event.getPayloadJson());
    }
}
