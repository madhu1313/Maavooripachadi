package com.maavooripachadi.payments.recovery;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;


@Entity
@Table(name = "payment_recovery_token", indexes = {
        @Index(name = "ix_recovery_token", columnList = "token", unique = true),
        @Index(name = "ix_recovery_order", columnList = "order_no")
})
public class RecoveryToken extends BaseEntity {


  @Column(nullable = false, unique = true, length = 64)
  private String token;


  @Column(name = "order_no", nullable = false, length = 64)
  private String orderNo;


  @Column(nullable = true)
  private OffsetDateTime expiresAt;


  @Column(nullable = false)
  private Boolean consumed = Boolean.FALSE;


  // getters & setters
  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }


  public String getOrderNo() { return orderNo; }
  public void setOrderNo(String orderNo) { this.orderNo = orderNo; }


  public OffsetDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }


  public Boolean getConsumed() { return consumed; }
  public void setConsumed(Boolean consumed) { this.consumed = consumed; }
}