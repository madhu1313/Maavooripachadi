package com.maavooripachadi.payments.gateway;


import org.springframework.stereotype.Service;


@Service
public class PaymentRouterService {
    private final RazorpayGateway razorpay;
    private final CashfreeGateway cashfree;


    public PaymentRouterService(RazorpayGateway razorpay, CashfreeGateway cashfree){
        this.razorpay = razorpay; this.cashfree = cashfree;
    }


    public PaymentGateway pick(GatewayName g){
        return switch (g){
            case RAZORPAY -> razorpay;
            case CASHFREE -> cashfree;
        };
    }
}