package com.maavooripachadi.privacy;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "cookie_preference", indexes = @Index(name = "ix_cookie_subject", columnList = "subject_id"))
public class CookiePreference extends BaseEntity {
    @Column(name = "subject_id")
    private String subjectId; // nullable for anonymous


    @Column(name = "session_id")
    private String sessionId; // device/session key


    private Boolean analytics = Boolean.FALSE;
    private Boolean marketing = Boolean.FALSE;
    private Boolean personalization = Boolean.FALSE;


    // getters/setters
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public Boolean getAnalytics() { return analytics; }
    public void setAnalytics(Boolean analytics) { this.analytics = analytics; }
    public Boolean getMarketing() { return marketing; }
    public void setMarketing(Boolean marketing) { this.marketing = marketing; }
    public Boolean getPersonalization() { return personalization; }
    public void setPersonalization(Boolean personalization) { this.personalization = personalization; }
}