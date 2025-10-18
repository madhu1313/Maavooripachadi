package com.maavooripachadi.risk.dto;

import com.maavooripachadi.risk.DenyType;
import jakarta.validation.constraints.*;

public class DenyListUpsertRequest {
    @NotNull private DenyType type;
    @NotBlank private String value; private String reason; private String source; private String expiresAt; // ISO offset
    public DenyType getType() { return type; }
    public void setType(DenyType type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
}
