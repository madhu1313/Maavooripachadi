package com.maavooripachadi.returns.dto;


public class ApproveReturnRequest {
    private String rmaCode; // if blank, server will generate
    private String adminNote;
    public String getRmaCode() { return rmaCode; }
    public void setRmaCode(String rmaCode) { this.rmaCode = rmaCode; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
}