package com.maavooripachadi.returns.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public class RefundDecisionRequest {
    @NotBlank private String orderNo;
    @Min(0) private int refundPaise; // total refund amount for request
    private String reason;
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getRefundPaise() { return refundPaise; }
    public void setRefundPaise(int refundPaise) { this.refundPaise = refundPaise; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}