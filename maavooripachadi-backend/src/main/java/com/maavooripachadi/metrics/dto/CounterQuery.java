package com.maavooripachadi.metrics.dto;


import com.maavooripachadi.metrics.MetricGranularity;
import jakarta.validation.constraints.NotBlank;


public class CounterQuery {
    @NotBlank private String name; private MetricGranularity granularity = MetricGranularity.MINUTE; private String from; private String to; private int page = 0; private int size = 200;
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public MetricGranularity getGranularity() { return granularity; } public void setGranularity(MetricGranularity granularity) { this.granularity = granularity; }
    public String getFrom() { return from; } public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; } public void setTo(String to) { this.to = to; }
    public int getPage() { return page; } public void setPage(int page) { this.page = page; }
    public int getSize() { return size; } public void setSize(int size) { this.size = size; }
}