package com.maavooripachadi.risk.dto;

import com.maavooripachadi.risk.RiskDecision;

public class EvaluateResponse {
    private int score; private RiskDecision decision; private String[] reasons; private Long caseId; private Long eventId;
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public RiskDecision getDecision() { return decision; }
    public void setDecision(RiskDecision decision) { this.decision = decision; }
    public String[] getReasons() { return reasons; }
    public void setReasons(String[] reasons) { this.reasons = reasons; }
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
}
