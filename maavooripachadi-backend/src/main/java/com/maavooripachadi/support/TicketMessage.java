package com.maavooripachadi.support;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "support_message", indexes = @Index(name = "ix_msg_ticket", columnList = "ticket_id"))
public class TicketMessage extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private SupportTicket ticket;

    private String author; // email/id (requester or agent)

    @Enumerated(EnumType.STRING)
    private MessageVisibility visibility = MessageVisibility.PUBLIC;

    @Lob
    private String body;

    // getters & setters
    public SupportTicket getTicket() { return ticket; }
    public void setTicket(SupportTicket ticket) { this.ticket = ticket; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public MessageVisibility getVisibility() { return visibility; }
    public void setVisibility(MessageVisibility visibility) { this.visibility = visibility; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
