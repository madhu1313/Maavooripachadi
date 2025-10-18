package com.maavooripachadi.payments.recovery.dto;


import jakarta.validation.constraints.NotBlank;


public class RecoveryIssueRequest {
    @NotBlank
    private String orderNo;


    // getters & setters
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}