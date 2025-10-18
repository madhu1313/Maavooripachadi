package com.maavooripachadi.security;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity(name = "SecurityAuditLog")
@Table(name = "sec_audit_log", indexes = @Index(name = "ix_audit_actor", columnList = "actor"))
public class AuditLog extends BaseEntity {
    private String actor; // email or system
    private String action; // e.g., LOGIN, ROLE_ASSIGN, ADMIN_API_CALL
    @Lob private String detailsJson;
    private String ip;

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
}
