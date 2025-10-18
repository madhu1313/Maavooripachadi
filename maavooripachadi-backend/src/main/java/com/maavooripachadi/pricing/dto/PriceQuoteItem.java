package com.maavooripachadi.pricing.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class PriceQuoteItem {
    @NotNull
    private Long variantId;
    @Min(1)
    private int qty;


    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}