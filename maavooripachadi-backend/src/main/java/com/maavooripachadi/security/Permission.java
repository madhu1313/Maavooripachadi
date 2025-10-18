package com.maavooripachadi.security;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "sec_permission", indexes = @Index(name = "ix_perm_name", columnList = "name", unique = true))
public class Permission extends BaseEntity {
  @Column(nullable = false, unique = true, length = 64)
  private String name; // e.g., PRICING_WRITE
  private String description;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
