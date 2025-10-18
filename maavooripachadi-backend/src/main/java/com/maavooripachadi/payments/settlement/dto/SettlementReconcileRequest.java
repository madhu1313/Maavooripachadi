package com.maavooripachadi.payments.settlement.dto;


public class SettlementReconcileRequest {
    private String fileId; // reconcile file id
    private String url; // CSV url


    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}