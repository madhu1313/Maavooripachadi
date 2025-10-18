package com.maavooripachadi.support;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity(name = "LegacySupportMessage")
public class SupportMessage extends BaseEntity {
  private Long ticketId;
  private String sender;

  @Lob
  private String body;

  @Lob
  private String attachments;

  public SupportMessage() {
  }

  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getAttachments() {
    return attachments;
  }

  public void setAttachments(String attachments) {
    this.attachments = attachments;
  }
}
