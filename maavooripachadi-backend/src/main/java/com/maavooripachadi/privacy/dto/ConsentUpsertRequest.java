package com.maavooripachadi.privacy.dto;


import com.maavooripachadi.privacy.ConsentCategory;
import com.maavooripachadi.privacy.ConsentStatus;
import jakarta.validation.constraints.NotNull;


public class ConsentUpsertRequest {
    private String subjectId; // optional if anonymous
    private String sessionId; // optional
    @NotNull private ConsentCategory category;
    @NotNull private ConsentStatus status;
    private String policyVersion; // optional
    private String source; // COOKIE_BANNER/SETTINGS/ADMIN


    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public ConsentCategory getCategory() { return category; }
    public void setCategory(ConsentCategory category) { this.category = category; }
    public ConsentStatus getStatus() { return status; }
    public void setStatus(ConsentStatus status) { this.status = status; }
    public String getPolicyVersion() { return policyVersion; }
    public void setPolicyVersion(String policyVersion) { this.policyVersion = policyVersion; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}