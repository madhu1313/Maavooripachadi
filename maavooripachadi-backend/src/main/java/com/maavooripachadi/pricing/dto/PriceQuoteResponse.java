package com.maavooripachadi.pricing.dto;


public class PriceQuoteResponse {
    private int subtotalPaise;
    private int discountPaise;
    private int taxPaise;
    private int shippingPaise;
    private int totalPaise;
    private String currency;


    public int getSubtotalPaise() { return subtotalPaise; }
    public void setSubtotalPaise(int subtotalPaise) { this.subtotalPaise = subtotalPaise; }
    public int getDiscountPaise() { return discountPaise; }
    public void setDiscountPaise(int discountPaise) { this.discountPaise = discountPaise; }
    public int getTaxPaise() { return taxPaise; }
    public void setTaxPaise(int taxPaise) { this.taxPaise = taxPaise; }
    public int getShippingPaise() { return shippingPaise; }
    public void setShippingPaise(int shippingPaise) { this.shippingPaise = shippingPaise; }
    public int getTotalPaise() { return totalPaise; }
    public void setTotalPaise(int totalPaise) { this.totalPaise = totalPaise; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}