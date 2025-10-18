package com.maavooripachadi.payments.gateway;


public interface PaymentGateway {
    /** Create a provider order and return provider order id. */
    String createGatewayOrder(PaymentAttempt attempt);


    /** Verify signature using payload + secret; return true if valid. */
    boolean verifySignature(String payload, String signature, String secretOverride);


    /** Capture/confirm the payment on provider (if required) and return provider payment id. */
    String capture(String gatewayPaymentId, int amountPaise);


    /** Issue a refund; return provider refund id. */
    String refund(String gatewayPaymentId, int amountPaise, String reason);
}