package com.maavooripachadi.logistics;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class WarehouseStock extends BaseEntity {
  @ManyToOne(optional = false)
  private Warehouse warehouse;

  private Long variantId;
  private Integer onHand = 0;
  private Integer reserved = 0;

  public WarehouseStock() {
  }

  public Warehouse getWarehouse() {
    return warehouse;
  }

  public void setWarehouse(Warehouse warehouse) {
    this.warehouse = warehouse;
  }

  public Long getVariantId() {
    return variantId;
  }

  public void setVariantId(Long variantId) {
    this.variantId = variantId;
  }

  public Integer getOnHand() {
    return onHand;
  }

  public void setOnHand(Integer onHand) {
    this.onHand = onHand;
  }

  public Integer getReserved() {
    return reserved;
  }

  public void setReserved(Integer reserved) {
    this.reserved = reserved;
  }
}
