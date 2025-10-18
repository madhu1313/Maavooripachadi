package com.maavooripachadi.payments.dto;


public class RecoveryValidateResponse {
    private boolean ok;
    private String orderNo;
    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}