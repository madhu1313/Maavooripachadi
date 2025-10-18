package com.maavooripachadi.metrics;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;


@Entity
@Table(name = "api_metric", indexes = {
        @Index(name = "ix_am_time", columnList = "occurred_at"),
        @Index(name = "ix_am_path", columnList = "path"),
        @Index(name = "ix_am_status", columnList = "status")
})
public class ApiMetric extends BaseEntity {


    private String method; // GET/POST
    private String path; // /api/v1/catalog/products
    private int status; // HTTP status
    private long durationMs; // time taken
    private String userId; // optional
    private String ip; // client ip


    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt = OffsetDateTime.now();


    // getters/setters
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
}