package com.maavooripachadi.privacy;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "consent_record", indexes = {
        @Index(name = "ix_consent_user", columnList = "subject_id"),
        @Index(name = "ix_consent_session", columnList = "session_id")
})
public class ConsentRecord extends BaseEntity {
    @Column(name = "subject_id")
    private String subjectId; // user id or email (nullable for anonymous)


    @Column(name = "session_id")
    private String sessionId; // anonymous session key / device id


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentCategory category;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus status = ConsentStatus.DENIED;


    @Column(name = "policy_version")
    private String policyVersion;


    @Column(name = "source")
    private String source; // e.g., COOKIE_BANNER, SETTINGS, ADMIN


    // getters/setters
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