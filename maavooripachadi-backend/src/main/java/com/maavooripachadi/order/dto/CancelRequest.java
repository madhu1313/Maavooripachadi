package com.maavooripachadi.order.dto;


import jakarta.validation.constraints.NotBlank;


public class CancelRequest {
    @NotBlank private String orderNo; private String reason;
    public String getOrderNo() { return orderNo; } public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getReason() { return reason; } public void setReason(String reason) { this.reason = reason; }
}