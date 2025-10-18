package com.maavooripachadi.order.dto;


import jakarta.validation.constraints.NotBlank;


public class MarkPaidRequest {
    @NotBlank private String orderNo; @NotBlank private String gateway; @NotBlank private String paymentRef;
    public String getOrderNo() { return orderNo; } public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getGateway() { return gateway; } public void setGateway(String gateway) { this.gateway = gateway; }
    public String getPaymentRef() { return paymentRef; } public void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }
}