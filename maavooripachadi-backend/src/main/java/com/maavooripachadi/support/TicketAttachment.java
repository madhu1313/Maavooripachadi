package com.maavooripachadi.support;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "support_attachment", indexes = @Index(name = "ix_attach_ticket", columnList = "ticket_id"))
public class TicketAttachment extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private SupportTicket ticket;

    private String fileName;
    private String url; // store location (S3/Cloud)
    private long sizeBytes;

    // getters & setters
    public SupportTicket getTicket() { return ticket; }
    public void setTicket(SupportTicket ticket) { this.ticket = ticket; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
}
