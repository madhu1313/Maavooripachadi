package com.maavooripachadi.metrics.dto;


import jakarta.validation.constraints.NotBlank;


public class EventRequest {
    @NotBlank private String name; private String unit; private Double value; private String tagsJson; private String occurredAt; // ISO string (optional)
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; } public void setUnit(String unit) { this.unit = unit; }
    public Double getValue() { return value; } public void setValue(Double value) { this.value = value; }
    public String getTagsJson() { return tagsJson; } public void setTagsJson(String tagsJson) { this.tagsJson = tagsJson; }
    public String getOccurredAt() { return occurredAt; } public void setOccurredAt(String occurredAt) { this.occurredAt = occurredAt; }
}