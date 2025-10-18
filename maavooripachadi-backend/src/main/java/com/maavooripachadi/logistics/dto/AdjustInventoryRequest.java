package com.maavooripachadi.logistics.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class AdjustInventoryRequest {
    @NotBlank private String warehouseCode;
    @NotNull private Long variantId;
    @NotNull private Integer deltaOnHand; // can be negative
    private String reason;


    // getters/setters
    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public Integer getDeltaOnHand() { return deltaOnHand; }
    public void setDeltaOnHand(Integer deltaOnHand) { this.deltaOnHand = deltaOnHand; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}