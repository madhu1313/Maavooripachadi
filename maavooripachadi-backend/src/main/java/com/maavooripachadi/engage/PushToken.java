package com.maavooripachadi.engage;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;


@Entity
@Table(name = "push_token")
public class PushToken extends BaseEntity {
  @Column(nullable = false)
  private String deviceId; // client-generated stable id


  @Column(nullable = false, unique = true)
  private String token; // FCM/APNs token


  private String platform; // ANDROID/IOS/WEB


  private Boolean enabled = Boolean.TRUE;


  private OffsetDateTime lastSeenAt;


  private String userId; // optional link to user if authenticated


  // ---- getters/setters ----
  public String getDeviceId() { return deviceId; }
  public void setDeviceId(String deviceId) { this.deviceId = deviceId; }


  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }


  public String getPlatform() { return platform; }
  public void setPlatform(String platform) { this.platform = platform; }


  public Boolean getEnabled() { return enabled; }
  public void setEnabled(Boolean enabled) { this.enabled = enabled; }


  public OffsetDateTime getLastSeenAt() { return lastSeenAt; }
  public void setLastSeenAt(OffsetDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }


  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }
}