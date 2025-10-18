package com.maavooripachadi.returns.dto;


public class ExchangeDecisionRequest {
    private String shippingMethod; // e.g., Shiprocket priority
    private String note;
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}