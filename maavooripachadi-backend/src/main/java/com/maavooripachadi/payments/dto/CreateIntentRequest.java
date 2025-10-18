package com.maavooripachadi.payments.dto;


import com.maavooripachadi.payments.gateway.GatewayName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class CreateIntentRequest {
    @NotBlank
    private String orderNo;
    @Min(1)
    private int amountPaise;
    @NotNull
    private GatewayName gateway = GatewayName.RAZORPAY;
    private String currency = "INR";


    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getAmountPaise() { return amountPaise; }
    public void setAmountPaise(int amountPaise) { this.amountPaise = amountPaise; }
    public GatewayName getGateway() { return gateway; }
    public void setGateway(GatewayName gateway) { this.gateway = gateway; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}