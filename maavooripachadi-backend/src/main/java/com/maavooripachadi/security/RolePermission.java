package com.maavooripachadi.security;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class RolePermission extends BaseEntity {
  private String role;
  private String permCode;

  public RolePermission() {
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getPermCode() {
    return permCode;
  }

  public void setPermCode(String permCode) {
    this.permCode = permCode;
  }
}
