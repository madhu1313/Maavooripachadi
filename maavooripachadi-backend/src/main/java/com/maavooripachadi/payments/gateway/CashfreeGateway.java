package com.maavooripachadi.payments.gateway;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class CashfreeGateway implements PaymentGateway {


    @Value("${payments.cashfree.key:cf_key}")
    private String key;


    @Value("${payments.cashfree.secret:cf_secret}")
    private String secret;


    @Override
    public String createGatewayOrder(PaymentAttempt attempt){
// TODO: call Cashfree Orders API
        return "cforder_" + System.currentTimeMillis();
    }


    @Override
    public boolean verifySignature(String payload, String signature, String secretOverride){
        String s = (secretOverride != null) ? secretOverride : secret;
        String expected = Hmac.sha256Hex(s, payload);
        return expected.equals(signature);
    }


    @Override
    public String capture(String gatewayPaymentId, int amountPaise){
// TODO: call Cashfree capture API (if applicable)
        return gatewayPaymentId;
    }


    @Override
    public String refund(String gatewayPaymentId, int amountPaise, String reason){
// TODO: call Cashfree refund API
        return "cfrfnd_" + System.currentTimeMillis();
    }
}