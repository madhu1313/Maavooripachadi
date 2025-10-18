package com.maavooripachadi.support;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "support_ticket", indexes = {
        @Index(name = "ix_ticket_no", columnList = "ticket_no", unique = true),
        @Index(name = "ix_ticket_status", columnList = "status"),
        @Index(name = "ix_ticket_assignee", columnList = "assignee")
})
public class SupportTicket extends BaseEntity {

  @Column(name = "ticket_no", nullable = false, unique = true)
  private String ticketNo; // e.g., ST-2025-000123

  @Column(nullable = false)
  private String subject;

  @Lob
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TicketStatus status = TicketStatus.OPEN;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TicketPriority priority = TicketPriority.MEDIUM;

  @Enumerated(EnumType.STRING)
  private TicketChannel channel = TicketChannel.WEB;

  private String requesterEmail;
  private String requesterName;

  private String assignee; // agent email/id

  private OffsetDateTime firstResponseDueAt;
  private OffsetDateTime resolveDueAt;
  private OffsetDateTime closedAt;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "support_ticket_tag", joinColumns = @JoinColumn(name = "ticket_id"))
  @Column(name = "tag")
  private Set<String> tags = new HashSet<>();

  // getters & setters
  public String getTicketNo() { return ticketNo; }
  public void setTicketNo(String ticketNo) { this.ticketNo = ticketNo; }
  public String getSubject() { return subject; }
  public void setSubject(String subject) { this.subject = subject; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public TicketStatus getStatus() { return status; }
  public void setStatus(TicketStatus status) { this.status = status; }
  public TicketPriority getPriority() { return priority; }
  public void setPriority(TicketPriority priority) { this.priority = priority; }
  public TicketChannel getChannel() { return channel; }
  public void setChannel(TicketChannel channel) { this.channel = channel; }
  public String getRequesterEmail() { return requesterEmail; }
  public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
  public String getRequesterName() { return requesterName; }
  public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
  public String getAssignee() { return assignee; }
  public void setAssignee(String assignee) { this.assignee = assignee; }
  public OffsetDateTime getFirstResponseDueAt() { return firstResponseDueAt; }
  public void setFirstResponseDueAt(OffsetDateTime firstResponseDueAt) { this.firstResponseDueAt = firstResponseDueAt; }
  public OffsetDateTime getResolveDueAt() { return resolveDueAt; }
  public void setResolveDueAt(OffsetDateTime resolveDueAt) { this.resolveDueAt = resolveDueAt; }
  public OffsetDateTime getClosedAt() { return closedAt; }
  public void setClosedAt(OffsetDateTime closedAt) { this.closedAt = closedAt; }
  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags; }
}
