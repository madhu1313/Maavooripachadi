package com.maavooripachadi.privacy;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "dsr_request", indexes = @Index(name = "ix_dsr_subject", columnList = "subject_id"))
public class DsrRequest extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DsrType type;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DsrStatus status = DsrStatus.OPEN;


    @Column(name = "subject_id", nullable = false)
    private String subjectId; // user id/email


    @Lob
    private String details; // free text by user/admin


    // getters/setters
    public DsrType getType() { return type; }
    public void setType(DsrType type) { this.type = type; }
    public DsrStatus getStatus() { return status; }
    public void setStatus(DsrStatus status) { this.status = status; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}