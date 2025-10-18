package com.maavooripachadi.risk;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "risk_velocity", indexes = @Index(name="ix_velo_key", columnList = "key_expr", unique = true))
public class VelocityWindow extends BaseEntity {
  @Column(name = "key_expr", nullable = false, length = 128)
  private String keyExpression; // e.g., "ip:{ip}", "email:{email}", "card:{card}"

  @Column(nullable = false)
  private int windowSeconds; // sliding window

  @Column(nullable = false)
  private int maxCount; // max events in window

  private String description;

  public String getKeyExpression() { return keyExpression; }
  public void setKeyExpression(String keyExpression) { this.keyExpression = keyExpression; }
  public int getWindowSeconds() { return windowSeconds; }
  public void setWindowSeconds(int windowSeconds) { this.windowSeconds = windowSeconds; }
  public int getMaxCount() { return maxCount; }
  public void setMaxCount(int maxCount) { this.maxCount = maxCount; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
