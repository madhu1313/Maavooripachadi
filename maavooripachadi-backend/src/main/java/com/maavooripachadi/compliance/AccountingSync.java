package com.maavooripachadi.compliance;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounting_sync")
public class AccountingSync extends BaseEntity {
  private String type; // ORDER, REFUND, PAYOUT
  private String refId; // foreign key value
  private String system; // e.g., ZOHO, TALLY
  private String status = "QUEUED"; // QUEUED, SENT, ERROR
  private String lastError;

  public AccountingSync() {
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRefId() {
    return refId;
  }

  public void setRefId(String refId) {
    this.refId = refId;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getLastError() {
    return lastError;
  }

  public void setLastError(String lastError) {
    this.lastError = lastError;
  }
}
