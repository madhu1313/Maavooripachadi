package com.maavooripachadi.payments.dto;


import jakarta.validation.constraints.NotBlank;


public class CaptureRequest {
    @NotBlank
    private String orderNo;
    @NotBlank
    private String gatewayPaymentId;
    @NotBlank
    private String gatewaySignature;


    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }
    public String getGatewaySignature() { return gatewaySignature; }
    public void setGatewaySignature(String gatewaySignature) { this.gatewaySignature = gatewaySignature; }
}