package com.maavooripachadi.payments.settlement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class SettlementLineTest {

    @Test
    void storesBatchAndFinancialDetails() {
        SettlementBatch batch = new SettlementBatch();
        batch.setGateway("CASHFREE");

        SettlementLine line = new SettlementLine();
        line.setBatch(batch);
        line.setOrderNo("ORD-999");
        line.setGatewayPaymentId("pay_G123");
        line.setAmountPaise(15000);
        line.setFeePaise(500);
        line.setTaxPaise(90);
        line.setStatus("SETTLED");

        assertSame(batch, line.getBatch());
        assertEquals("ORD-999", line.getOrderNo());
        assertEquals("pay_G123", line.getGatewayPaymentId());
        assertEquals(15000, line.getAmountPaise());
        assertEquals(500, line.getFeePaise());
        assertEquals(90, line.getTaxPaise());
        assertEquals("SETTLED", line.getStatus());
    }
}
