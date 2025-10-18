package com.maavooripachadi.payments.recovery.dto;


public class RecoveryIssueResponse {
    private String token;
    private String url; // absolute or relative deep‑link for resume payment


    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }


    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}