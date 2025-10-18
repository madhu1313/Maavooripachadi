package com.maavooripachadi.payments.settlement.dto;


public class SettlementIngestRequest {
    private String gateway; // RAZORPAY/CASHFREE
    private String fileId; // reference to uploaded file
    private String url; // temp URL to fetch CSV
    private String originalName;
    private String checksum; // optional
    private String payoutDate; // yyyy-MM-dd


    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    public String getPayoutDate() { return payoutDate; }
    public void setPayoutDate(String payoutDate) { this.payoutDate = payoutDate; }
}