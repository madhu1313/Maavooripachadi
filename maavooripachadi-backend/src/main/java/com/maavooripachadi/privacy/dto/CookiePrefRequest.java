package com.maavooripachadi.privacy.dto;


public class CookiePrefRequest {
    private String subjectId; private String sessionId;
    private Boolean analytics; private Boolean marketing; private Boolean personalization;
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