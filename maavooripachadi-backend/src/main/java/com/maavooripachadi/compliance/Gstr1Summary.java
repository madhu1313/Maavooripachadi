package com.maavooripachadi.compliance;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "gstr1_summary")
public class Gstr1Summary extends BaseEntity {
  private String period; // YYYY-MM
  private Integer b2cCount;
  private Integer b2cTaxablePaise;
  private Integer b2cTaxPaise;
  private Integer b2bCount;
  private Integer b2bTaxablePaise;
  private Integer b2bTaxPaise;

  public Gstr1Summary() {
  }

  public String getPeriod() {
    return period;
  }

  public void setPeriod(String period) {
    this.period = period;
  }

  public Integer getB2cCount() {
    return b2cCount;
  }

  public void setB2cCount(Integer b2cCount) {
    this.b2cCount = b2cCount;
  }

  public Integer getB2cTaxablePaise() {
    return b2cTaxablePaise;
  }

  public void setB2cTaxablePaise(Integer b2cTaxablePaise) {
    this.b2cTaxablePaise = b2cTaxablePaise;
  }

  public Integer getB2cTaxPaise() {
    return b2cTaxPaise;
  }

  public void setB2cTaxPaise(Integer b2cTaxPaise) {
    this.b2cTaxPaise = b2cTaxPaise;
  }

  public Integer getB2bCount() {
    return b2bCount;
  }

  public void setB2bCount(Integer b2bCount) {
    this.b2bCount = b2bCount;
  }

  public Integer getB2bTaxablePaise() {
    return b2bTaxablePaise;
  }

  public void setB2bTaxablePaise(Integer b2bTaxablePaise) {
    this.b2bTaxablePaise = b2bTaxablePaise;
  }

  public Integer getB2bTaxPaise() {
    return b2bTaxPaise;
  }

  public void setB2bTaxPaise(Integer b2bTaxPaise) {
    this.b2bTaxPaise = b2bTaxPaise;
  }
}
