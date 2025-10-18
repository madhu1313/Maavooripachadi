package com.maavooripachadi.payments.dto;


import jakarta.validation.constraints.NotBlank;


public class RecoveryIssueRequest {
    @NotBlank
    private String orderNo;
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}