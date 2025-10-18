package com.maavooripachadi.pricing;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;


@Entity
@Table(name = "currency_rate", uniqueConstraints = @UniqueConstraint(name = "uq_fx_pair", columnNames = {"from_ccy", "to_ccy"}))
public class CurrencyRate extends BaseEntity {
  @Column(name = "from_ccy", nullable = false, length = 8)
  private String from;
  @Column(name = "to_ccy", nullable = false, length = 8)
  private String to;
  @Column(nullable = false)
  private double rate; // multiply by this to convert from->to
  private OffsetDateTime updatedAt;


  public String getFrom() { return from; }
  public void setFrom(String from) { this.from = from; }
  public String getTo() { return to; }
  public void setTo(String to) { this.to = to; }
  public double getRate() { return rate; }
  public void setRate(double rate) { this.rate = rate; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}