package com.maavooripachadi.payments.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public class RefundRequest {
    @NotBlank
    private String orderNo;
    @Min(1)
    private int amountPaise;
    private String reason;


    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getAmountPaise() { return amountPaise; }
    public void setAmountPaise(int amountPaise) { this.amountPaise = amountPaise; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}