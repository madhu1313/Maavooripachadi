package com.maavooripachadi.risk;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "risk_score", indexes = @Index(name = "ix_risk_score_evt", columnList = "event_id"))
public class RiskScore extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private RiskEvent event;

    private int score;

    @Enumerated(EnumType.STRING)
    private RiskDecision decision;

    @Lob
    private String reasonsJson; // array of reasons

    public RiskEvent getEvent() { return event; }
    public void setEvent(RiskEvent event) { this.event = event; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public RiskDecision getDecision() { return decision; }
    public void setDecision(RiskDecision decision) { this.decision = decision; }
    public String getReasonsJson() { return reasonsJson; }
    public void setReasonsJson(String reasonsJson) { this.reasonsJson = reasonsJson; }
}
