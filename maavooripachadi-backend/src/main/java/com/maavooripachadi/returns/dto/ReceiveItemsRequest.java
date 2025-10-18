package com.maavooripachadi.returns.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class ReceiveItemsRequest {
    @NotNull private Long returnItemId;
    @Min(0) private int receivedQty;
    private boolean restock; // whether to increase inventory
    private String note;
    public Long getReturnItemId() { return returnItemId; }
    public void setReturnItemId(Long returnItemId) { this.returnItemId = returnItemId; }
    public int getReceivedQty() { return receivedQty; }
    public void setReceivedQty(int receivedQty) { this.receivedQty = receivedQty; }
    public boolean isRestock() { return restock; }
    public void setRestock(boolean restock) { this.restock = restock; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}