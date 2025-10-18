package com.maavooripachadi.metrics;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;


@Entity
@Table(name = "metric_counter", indexes = {
        @Index(name = "ix_mc_name_window", columnList = "name, window_start, granularity")
})
public class MetricCounter extends BaseEntity {


    @Column(nullable = false)
    private String name; // same as event name


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricGranularity granularity = MetricGranularity.MINUTE;


    @Column(name = "window_start", nullable = false)
    private OffsetDateTime windowStart; // inclusive


    @Column(name = "window_end", nullable = false)
    private OffsetDateTime windowEnd; // exclusive


    @Column(nullable = false)
    private long count;


    private double sum;
    private Double minVal;
    private Double maxVal;


    private String keyHash; // hash of tags bucket
    @Lob private String tagsJson; // canonicalized tags for this counter bucket


    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public MetricGranularity getGranularity() { return granularity; }
    public void setGranularity(MetricGranularity granularity) { this.granularity = granularity; }
    public OffsetDateTime getWindowStart() { return windowStart; }
    public void setWindowStart(OffsetDateTime windowStart) { this.windowStart = windowStart; }
    public OffsetDateTime getWindowEnd() { return windowEnd; }
    public void setWindowEnd(OffsetDateTime windowEnd) { this.windowEnd = windowEnd; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
    public double getSum() { return sum; }
    public void setSum(double sum) { this.sum = sum; }
    public Double getMinVal() { return minVal; }
    public void setMinVal(Double minVal) { this.minVal = minVal; }
    public Double getMaxVal() { return maxVal; }
    public void setMaxVal(Double maxVal) { this.maxVal = maxVal; }
    public String getKeyHash() { return keyHash; }
    public void setKeyHash(String keyHash) { this.keyHash = keyHash; }
    public String getTagsJson() { return tagsJson; }
    public void setTagsJson(String tagsJson) { this.tagsJson = tagsJson; }
}