package com.maavooripachadi.metrics;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;


@Entity
@Table(name = "metric_event", indexes = {
        @Index(name = "ix_me_name_time", columnList = "name, occurred_at")
})
public class MetricEvent extends BaseEntity {


    @Column(nullable = false)
    private String name; // e.g., checkout_start, payment_success


    private String unit; // e.g., ms, count, rupees


    @Column(nullable = false)
    private double value; // value of the event


    @Lob
    private String tagsJson; // JSON string of tags (k->v)


    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt = OffsetDateTime.now();


    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public String getTagsJson() { return tagsJson; }
    public void setTagsJson(String tagsJson) { this.tagsJson = tagsJson; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
}