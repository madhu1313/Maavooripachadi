package com.maavooripachadi.returns;


import com.maavooripachadi.payments.PaymentsService;
import com.maavooripachadi.payments.dto.RefundRequest;
import org.springframework.stereotype.Component;


@Component
public class PaymentsAdapter implements PaymentsPort {
    private final PaymentsService payments;
    public PaymentsAdapter(PaymentsService payments){ this.payments = payments; }
    @Override public String refund(String orderNo, int amountPaise, String reason){
        RefundRequest r = new RefundRequest(); r.setOrderNo(orderNo); r.setAmountPaise(amountPaise); r.setReason(reason);
        var saved = payments.refund(r); // returns PaymentRefund
        return saved.getGatewayRefundId();
    }
}