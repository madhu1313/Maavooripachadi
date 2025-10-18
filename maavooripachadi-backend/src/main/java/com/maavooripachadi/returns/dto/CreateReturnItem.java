package com.maavooripachadi.returns.dto;


import com.maavooripachadi.returns.ReturnReason;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class CreateReturnItem {
    @NotNull private Long orderLineId;
    @NotNull private Long variantId;
    @NotNull private ReturnReason reason;
    @Min(1) private int qty;
    public Long getOrderLineId() { return orderLineId; }
    public void setOrderLineId(Long orderLineId) { this.orderLineId = orderLineId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public ReturnReason getReason() { return reason; }
    public void setReason(ReturnReason reason) { this.reason = reason; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}