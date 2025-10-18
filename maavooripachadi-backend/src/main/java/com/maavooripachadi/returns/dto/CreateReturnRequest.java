package com.maavooripachadi.returns.dto;


import com.maavooripachadi.returns.RefundMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;


public class CreateReturnRequest {
    @NotBlank private String orderNo;
    @Email private String customerEmail;
    @NotEmpty private List<CreateReturnItem> items;
    private RefundMethod refundMethod = RefundMethod.ORIGINAL_PAYMENT;
    private String notes;
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public List<CreateReturnItem> getItems() { return items; }
    public void setItems(List<CreateReturnItem> items) { this.items = items; }
    public RefundMethod getRefundMethod() { return refundMethod; }
    public void setRefundMethod(RefundMethod refundMethod) { this.refundMethod = refundMethod; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}