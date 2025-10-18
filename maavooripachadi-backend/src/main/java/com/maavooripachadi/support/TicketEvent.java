package com.maavooripachadi.support;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "support_event", indexes = @Index(name = "ix_evt_ticket", columnList = "ticket_id"))
public class TicketEvent extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private SupportTicket ticket;

    private String kind; // CREATED, ASSIGNED, STATUS, TAG_ADDED, SLA_BREACH, MESSAGE

    @Lob
    private String payloadJson;

    // getters & setters
    public SupportTicket getTicket() { return ticket; }
    public void setTicket(SupportTicket ticket) { this.ticket = ticket; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}
