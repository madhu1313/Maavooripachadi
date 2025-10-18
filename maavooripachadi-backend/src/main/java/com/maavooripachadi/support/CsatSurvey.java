package com.maavooripachadi.support;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "support_csat", indexes = @Index(name = "ix_csat_ticket", columnList = "ticket_id", unique = true))
public class CsatSurvey extends BaseEntity {
    @OneToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private SupportTicket ticket;

    private int rating; // 1..5
    @Lob private String comment;

    // getters & setters
    public SupportTicket getTicket() { return ticket; }
    public void setTicket(SupportTicket ticket) { this.ticket = ticket; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
