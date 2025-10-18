package com.maavooripachadi.order.dto;


import jakarta.validation.constraints.*;


public class CheckoutRequest {
    @NotBlank private String sessionId; // cart session id
    @NotBlank private String customerEmail; @NotBlank private String customerPhone; @NotBlank private String customerName;
    private String shipName; private String shipPhone; @NotBlank private String shipLine1; private String shipLine2; @NotBlank private String shipCity; @NotBlank private String shipState; @NotBlank private String shipPincode; private String shipCountry = "IN";
    private String couponCode; private String notes; private String paymentGateway = "razorpay";


    // getters/setters
    public String getSessionId() { return sessionId; } public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getCustomerEmail() { return customerEmail; } public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; } public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerName() { return customerName; } public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getShipName() { return shipName; } public void setShipName(String shipName) { this.shipName = shipName; }
    public String getShipPhone() { return shipPhone; } public void setShipPhone(String shipPhone) { this.shipPhone = shipPhone; }
    public String getShipLine1() { return shipLine1; } public void setShipLine1(String shipLine1) { this.shipLine1 = shipLine1; }
    public String getShipLine2() { return shipLine2; } public void setShipLine2(String shipLine2) { this.shipLine2 = shipLine2; }
    public String getShipCity() { return shipCity; } public void setShipCity(String shipCity) { this.shipCity = shipCity; }
    public String getShipState() { return shipState; } public void setShipState(String shipState) { this.shipState = shipState; }
    public String getShipPincode() { return shipPincode; } public void setShipPincode(String shipPincode) { this.shipPincode = shipPincode; }
    public String getShipCountry() { return shipCountry; } public void setShipCountry(String shipCountry) { this.shipCountry = shipCountry; }
    public String getCouponCode() { return couponCode; } public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public String getNotes() { return notes; } public void setNotes(String notes) { this.notes = notes; }
    public String getPaymentGateway() { return paymentGateway; } public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }
}
