package com.maavooripachadi.privacy.dto;


import com.maavooripachadi.privacy.DsrStatus;
import jakarta.validation.constraints.NotNull;


public class DsrDecisionRequest {
    @NotNull private DsrStatus status;
    private String details;
    public DsrStatus getStatus() { return status; }
    public void setStatus(DsrStatus status) { this.status = status; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}