package com.maavooripachadi.engage;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "outbound_template")
public class OutboundTemplate extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String code; // unique template key, e.g., ORDER_CONF


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OutboundChannel channel; // EMAIL/SMS/PUSH


  private String locale; // e.g., en_IN


  private String subject; // email subject (optional for SMS/PUSH)


  @Lob
  private String bodyHtml; // for EMAIL (HTML)


  @Lob
  private String bodyText; // for SMS/PUSH (and fallback for email)


  private Boolean enabled = Boolean.TRUE;


  private String createdBy;


  // ---- getters/setters ----
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }


  public OutboundChannel getChannel() { return channel; }
  public void setChannel(OutboundChannel channel) { this.channel = channel; }


  public String getLocale() { return locale; }
  public void setLocale(String locale) { this.locale = locale; }


  public String getSubject() { return subject; }
  public void setSubject(String subject) { this.subject = subject; }


  public String getBodyHtml() { return bodyHtml; }
  public void setBodyHtml(String bodyHtml) { this.bodyHtml = bodyHtml; }


  public String getBodyText() { return bodyText; }
  public void setBodyText(String bodyText) { this.bodyText = bodyText; }


  public Boolean getEnabled() { return enabled; }
  public void setEnabled(Boolean enabled) { this.enabled = enabled; }


  public String getCreatedBy() { return createdBy; }
  public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}