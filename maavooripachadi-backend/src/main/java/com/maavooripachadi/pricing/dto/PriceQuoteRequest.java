package com.maavooripachadi.pricing.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class PriceQuoteRequest {
    @NotNull
    private java.util.List<PriceQuoteItem> items;
    private String priceListName; // optional, defaults to default list
    @NotBlank
    private String pincode;
    private String couponCode;
    private String region; // e.g., state code for tax rule matching


    public java.util.List<PriceQuoteItem> getItems() { return items; }
    public void setItems(java.util.List<PriceQuoteItem> items) { this.items = items; }
    public String getPriceListName() { return priceListName; }
    public void setPriceListName(String priceListName) { this.priceListName = priceListName; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}