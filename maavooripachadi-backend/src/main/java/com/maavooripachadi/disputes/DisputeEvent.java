package com.maavooripachadi.disputes;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "dispute_event")
public class DisputeEvent extends BaseEntity {


    @ManyToOne(optional = false)
    private Dispute dispute;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeEventType type; // OPENED, EVIDENCE_SUBMITTED, STATUS_CHANGE, NOTE


    @Lob
    private String payload; // JSON evidence snapshot or reason


    // ---- getters/setters ----
    public Dispute getDispute() { return dispute; }
    public void setDispute(Dispute dispute) { this.dispute = dispute; }


    public DisputeEventType getType() { return type; }
    public void setType(DisputeEventType type) { this.type = type; }


    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}