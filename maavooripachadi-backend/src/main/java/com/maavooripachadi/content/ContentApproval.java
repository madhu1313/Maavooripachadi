package com.maavooripachadi.content;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "content_approval")
public class ContentApproval extends BaseEntity {
  @Column(nullable = false)
  private String type; // "RECIPE" | "BLOG"

  @Column(nullable = false)
  private Long refId; // id of recipe/blog

  private String submittedBy;
  private String decidedBy;
  private String status = "PENDING"; // PENDING/APPROVED/REJECTED

  @Lob
  private String note;

  public ContentApproval() {
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Long getRefId() {
    return refId;
  }

  public void setRefId(Long refId) {
    this.refId = refId;
  }

  public String getSubmittedBy() {
    return submittedBy;
  }

  public void setSubmittedBy(String submittedBy) {
    this.submittedBy = submittedBy;
  }

  public String getDecidedBy() {
    return decidedBy;
  }

  public void setDecidedBy(String decidedBy) {
    this.decidedBy = decidedBy;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }
}
