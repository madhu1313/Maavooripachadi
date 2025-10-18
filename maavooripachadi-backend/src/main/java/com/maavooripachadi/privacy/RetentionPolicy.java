package com.maavooripachadi.privacy;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class RetentionPolicy extends BaseEntity {
  private String tableName;
  private String field;
  private Integer days;
  private String strategy;

  public RetentionPolicy() {
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public Integer getDays() {
    return days;
  }

  public void setDays(Integer days) {
    this.days = days;
  }

  public String getStrategy() {
    return strategy;
  }

  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }
}
