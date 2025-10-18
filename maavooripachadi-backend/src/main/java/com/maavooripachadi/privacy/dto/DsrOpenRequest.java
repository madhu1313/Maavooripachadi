package com.maavooripachadi.privacy.dto;


import com.maavooripachadi.privacy.DsrType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class DsrOpenRequest {
    @NotNull private DsrType type;
    @NotBlank private String subjectId; // email or account id
    private String details;
    public DsrType getType() { return type; }
    public void setType(DsrType type) { this.type = type; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}