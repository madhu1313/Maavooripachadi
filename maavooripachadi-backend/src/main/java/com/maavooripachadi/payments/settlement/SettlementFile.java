package com.maavooripachadi.payments.settlement;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class SettlementFile extends BaseEntity {
  private String gateway;
  private String period;
  private String path;
  private String status = "QUEUED";

  public SettlementFile() {
  }

  public String getGateway() {
    return gateway;
  }

  public void setGateway(String gateway) {
    this.gateway = gateway;
  }

  public String getPeriod() {
    return period;
  }

  public void setPeriod(String period) {
    this.period = period;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
