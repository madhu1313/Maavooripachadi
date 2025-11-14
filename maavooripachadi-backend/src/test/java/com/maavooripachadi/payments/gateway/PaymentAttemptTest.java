package com.maavooripachadi.payments.gateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PaymentAttemptTest {

    @Test
    void settersCoverAllFields() {
        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setOrderNo("ORD-1");
        attempt.setGateway(GatewayName.CASHFREE);
        attempt.setStatus(AttemptStatus.CAPTURED);
        attempt.setAmountPaise(5000);
        attempt.setCurrency("USD");
        attempt.setGatewayOrderId("order_1");
        attempt.setGatewayPaymentId("pay_1");
        attempt.setGatewaySignature("sig");
        attempt.setMetaJson("{\"key\":\"value\"}");

        assertEquals("ORD-1", attempt.getOrderNo());
        assertSame(GatewayName.CASHFREE, attempt.getGateway());
        assertSame(AttemptStatus.CAPTURED, attempt.getStatus());
        assertEquals(5000, attempt.getAmountPaise());
        assertEquals("USD", attempt.getCurrency());
        assertEquals("order_1", attempt.getGatewayOrderId());
        assertEquals("pay_1", attempt.getGatewayPaymentId());
        assertEquals("sig", attempt.getGatewaySignature());
        assertEquals("{\"key\":\"value\"}", attempt.getMetaJson());
    }

    @Test
    void defaultsArePopulated() {
        PaymentAttempt attempt = new PaymentAttempt();
        assertEquals(GatewayName.RAZORPAY, attempt.getGateway());
        assertEquals(AttemptStatus.CREATED, attempt.getStatus());
        assertEquals("INR", attempt.getCurrency());
    }
}
