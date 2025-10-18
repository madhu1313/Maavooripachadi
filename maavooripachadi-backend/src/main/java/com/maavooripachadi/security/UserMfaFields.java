package com.maavooripachadi.security;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class UserMfaFields extends BaseEntity {
  @Column(unique = true)
  private Long userId;

  private String mfaSecret;
  private Boolean mfaEnabled = false;

  public UserMfaFields() {
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getMfaSecret() {
    return mfaSecret;
  }

  public void setMfaSecret(String mfaSecret) {
    this.mfaSecret = mfaSecret;
  }

  public Boolean getMfaEnabled() {
    return mfaEnabled;
  }

  public void setMfaEnabled(Boolean mfaEnabled) {
    this.mfaEnabled = mfaEnabled;
  }
}
