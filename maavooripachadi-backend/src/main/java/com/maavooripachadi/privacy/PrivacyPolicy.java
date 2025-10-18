package com.maavooripachadi.privacy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "privacy_policy")
public class PrivacyPolicy extends BaseEntity {
    @Column(name = "version", nullable = false, unique = true)
    private String policyVersion; // e.g., 2025-01

    @Lob
    @Column(nullable = false)
    private String markdown; // policy content markdown

    @Column(nullable = false)
    private Boolean active = Boolean.FALSE;

    // getters/setters
    @JsonProperty("version")
    public String getPolicyVersion() { return policyVersion; }

    @JsonProperty("version")
    public void setPolicyVersion(String version) { this.policyVersion = version; }

    public String getMarkdown() { return markdown; }
    public void setMarkdown(String markdown) { this.markdown = markdown; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
