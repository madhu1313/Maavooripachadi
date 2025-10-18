package com.maavooripachadi.compliance;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ewaybill")
public class Ewaybill extends BaseEntity {
  private String orderNo;
  private String ewbNo;
  private OffsetDateTime validUpto;
  private String vehicleNo;
  private Integer distanceKm;

  public Ewaybill() {
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getEwbNo() {
    return ewbNo;
  }

  public void setEwbNo(String ewbNo) {
    this.ewbNo = ewbNo;
  }

  public OffsetDateTime getValidUpto() {
    return validUpto;
  }

  public void setValidUpto(OffsetDateTime validUpto) {
    this.validUpto = validUpto;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Integer getDistanceKm() {
    return distanceKm;
  }

  public void setDistanceKm(Integer distanceKm) {
    this.distanceKm = distanceKm;
  }
}
