package com.maavooripachadi.order.dto;


import com.maavooripachadi.order.OrderItem;


public class OrderItemResponse {
    private Long id; private Long variantId; private String sku; private String title; private int qty; private int unitPricePaise; private int lineTotalPaise;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getVariantId() { return variantId; } public void setVariantId(Long variantId) { this.variantId = variantId; }
    public String getSku() { return sku; } public void setSku(String sku) { this.sku = sku; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public int getQty() { return qty; } public void setQty(int qty) { this.qty = qty; }
    public int getUnitPricePaise() { return unitPricePaise; } public void setUnitPricePaise(int unitPricePaise) { this.unitPricePaise = unitPricePaise; }
    public int getLineTotalPaise() { return lineTotalPaise; } public void setLineTotalPaise(int lineTotalPaise) { this.lineTotalPaise = lineTotalPaise; }
    public static OrderItemResponse from(OrderItem i){ OrderItemResponse r=new OrderItemResponse(); r.setId(i.getId()); r.setVariantId(i.getVariantId()); r.setSku(i.getSku()); r.setTitle(i.getTitle()); r.setQty(i.getQty()); r.setUnitPricePaise(i.getUnitPricePaise()); r.setLineTotalPaise(i.getLineTotalPaise()); return r; }
}