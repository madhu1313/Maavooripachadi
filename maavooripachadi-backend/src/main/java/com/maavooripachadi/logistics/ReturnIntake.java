package com.maavooripachadi.logistics;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class ReturnIntake extends BaseEntity {
  private Long returnId;
  private Long warehouseId;
  private String condition;
  private Boolean restockable = false;
  private Long variantId;
  private Integer qty;
  private String processedBy;
  private String note;

  public ReturnIntake() {
  }

  public Long getReturnId() {
    return returnId;
  }

  public void setReturnId(Long returnId) {
    this.returnId = returnId;
  }

  public Long getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(Long warehouseId) {
    this.warehouseId = warehouseId;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public Boolean getRestockable() {
    return restockable;
  }

  public void setRestockable(Boolean restockable) {
    this.restockable = restockable;
  }

  public Long getVariantId() {
    return variantId;
  }

  public void setVariantId(Long variantId) {
    this.variantId = variantId;
  }

  public Integer getQty() {
    return qty;
  }

  public void setQty(Integer qty) {
    this.qty = qty;
  }

  public String getProcessedBy() {
    return processedBy;
  }

  public void setProcessedBy(String processedBy) {
    this.processedBy = processedBy;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}
