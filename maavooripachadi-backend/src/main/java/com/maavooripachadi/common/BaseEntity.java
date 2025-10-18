package com.maavooripachadi.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;
@Column(name = "created_at", nullable = false, updatable = false)
protected OffsetDateTime createdAt;


/**
 * Last update timestamp (UTC). Set automatically on update.
 */
@Column(name = "updated_at", nullable = false)
protected OffsetDateTime updatedAt;


/**
 * Optimistic locking version.
 */
@Version
@JsonIgnore
@Column(name = "row_version", nullable = false)
protected long version;


@PrePersist
protected void onCreate() {
  OffsetDateTime now = OffsetDateTime.now();
  this.createdAt = now;
  this.updatedAt = now;
}


@PreUpdate
protected void onUpdate() {
  this.updatedAt = OffsetDateTime.now();
}


public Long getId() { return id; }
public OffsetDateTime getCreatedAt() { return createdAt; }
public OffsetDateTime getUpdatedAt() { return updatedAt; }
public long getVersion() { return version; }


// ---- Equality on identifier ----
@Override
public boolean equals(Object o) {
  if (this == o) return true;
  if (o == null || getClass() != o.getClass()) return false;
  BaseEntity that = (BaseEntity) o;
  return id != null && Objects.equals(id, that.id);
}


@Override
public int hashCode() {
  return 31;
}
}