package com.maavooripachadi.risk;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "risk_case")
public class RiskCase extends BaseEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "event_id", unique = true)
    private RiskEvent event;

    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.OPEN;

    private String assignedTo; // user id/email

    @Lob
    private String notes;

    public RiskEvent getEvent() { return event; }
    public void setEvent(RiskEvent event) { this.event = event; }
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
