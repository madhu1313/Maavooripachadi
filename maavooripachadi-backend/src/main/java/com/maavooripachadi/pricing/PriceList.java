package com.maavooripachadi.pricing;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "price_list", indexes = @Index(name = "ix_pl_name", columnList = "name", unique = true))
public class PriceList extends BaseEntity {
  @Column(nullable = false, unique = true, length = 64)
  private String name; // e.g., "DEFAULT_INR"
  @Column(nullable = false, length = 8)
  private String currency = "INR";
  @Column(nullable = false)
  private Boolean active = Boolean.TRUE;
  @Column(nullable = false)
  private Boolean isDefault = Boolean.FALSE;


  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public Boolean getActive() { return active; }
  public void setActive(Boolean active) { this.active = active; }
  public Boolean getIsDefault() { return isDefault; }
  public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}