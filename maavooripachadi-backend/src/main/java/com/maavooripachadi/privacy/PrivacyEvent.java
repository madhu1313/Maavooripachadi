package com.maavooripachadi.privacy;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "privacy_event", indexes = @Index(name = "ix_privacy_event", columnList = "subject_id"))
public class PrivacyEvent extends BaseEntity {
    @Column(name = "subject_id")
    private String subjectId; // nullable for anon


    private String kind; // CONSENT_CHANGED, DSR_OPENED, DSR_DECIDED, POLICY_PUBLISHED


    @Lob
    private String payloadJson;


    // getters/setters
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}