package com.maavooripachadi.payments.settlement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettlementFileTest {

    @Test
    void settersUpdateFields() {
        SettlementFile file = new SettlementFile();
        file.setGateway("RAZORPAY");
        file.setPeriod("2025-11");
        file.setPath("/tmp/file.csv");
        file.setStatus("PROCESSING");

        assertEquals("RAZORPAY", file.getGateway());
        assertEquals("2025-11", file.getPeriod());
        assertEquals("/tmp/file.csv", file.getPath());
        assertEquals("PROCESSING", file.getStatus());
    }

    @Test
    void defaultStatusIsQueued() {
        SettlementFile file = new SettlementFile();
        assertEquals("QUEUED", file.getStatus());
    }
}
