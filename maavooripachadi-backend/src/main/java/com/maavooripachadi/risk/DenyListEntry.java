package com.maavooripachadi.risk;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "risk_denylist", indexes = {@Index(name="ix_deny_type_value", columnList = "type,value", unique = true)})
public class DenyListEntry extends BaseEntity {
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private DenyType type;

  @Column(nullable = false, length = 255)
  private String value;

  private String reason;
  private String source; // admin/email/chargeback
  private OffsetDateTime expiresAt; // nullable => never

  public DenyType getType() { return type; }
  public void setType(DenyType type) { this.type = type; }
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }
  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }
  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }
  public OffsetDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
}
