package com.maavooripachadi.payments.settlement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SettlementEntryTest {

    @Test
    void settersPopulateAllFields() {
        SettlementEntry entry = new SettlementEntry();
        entry.setFileId(99L);
        entry.setPaymentId("pay_1");
        entry.setOrderNo("ORD-1");
        entry.setEvent("CAPTURED");
        entry.setAmount(1000);
        entry.setFee(50);
        entry.setTax(9);
        entry.setCurrency("USD");
        entry.setProcessed(true);

        assertEquals(99L, entry.getFileId());
        assertEquals("pay_1", entry.getPaymentId());
        assertEquals("ORD-1", entry.getOrderNo());
        assertEquals("CAPTURED", entry.getEvent());
        assertEquals(1000, entry.getAmount());
        assertEquals(50, entry.getFee());
        assertEquals(9, entry.getTax());
        assertEquals("USD", entry.getCurrency());
        assertEquals(true, entry.getProcessed());
    }

    @Test
    void defaultsAreReasonable() {
        SettlementEntry entry = new SettlementEntry();
        assertEquals("INR", entry.getCurrency());
        assertFalse(entry.getProcessed());
    }
}
