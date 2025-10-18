package com.maavooripachadi.payments.settlement.dto;


public class BatchSummaryResponse {
    private Long id; private String gateway; private String payoutDate; private int countTxns; private int totalAmountPaise;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getGateway() { return gateway; } public void setGateway(String gateway) { this.gateway = gateway; }
    public String getPayoutDate() { return payoutDate; } public void setPayoutDate(String payoutDate) { this.payoutDate = payoutDate; }
    public int getCountTxns() { return countTxns; } public void setCountTxns(int countTxns) { this.countTxns = countTxns; }
    public int getTotalAmountPaise() { return totalAmountPaise; } public void setTotalAmountPaise(int totalAmountPaise) { this.totalAmountPaise = totalAmountPaise; }
}