package com.maavooripachadi.pricing;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;


@Entity
@Table(name = "coupon", indexes = @Index(name = "ix_coupon_code", columnList = "code", unique = true))
public class Coupon extends BaseEntity {
    @Column(nullable = false, unique = true, length = 64)
    private String code;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type = CouponType.PERCENT;


    @Column(nullable = false)
    private int value; // percent (e.g., 10) or flat paise (e.g., 5000)


    private Integer maxDiscountPaise; // cap for percent type
    private Integer minSubtotalPaise; // threshold to apply


    private OffsetDateTime startsAt; // nullable => always
    private OffsetDateTime endsAt; // nullable => no end


    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;


    private Integer usageLimit; // null => infinite
    @Column(nullable = false)
    private Integer usageCount = 0;


    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public CouponType getType() { return type; }
    public void setType(CouponType type) { this.type = type; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public Integer getMaxDiscountPaise() { return maxDiscountPaise; }
    public void setMaxDiscountPaise(Integer maxDiscountPaise) { this.maxDiscountPaise = maxDiscountPaise; }
    public Integer getMinSubtotalPaise() { return minSubtotalPaise; }
    public void setMinSubtotalPaise(Integer minSubtotalPaise) { this.minSubtotalPaise = minSubtotalPaise; }
    public OffsetDateTime getStartsAt() { return startsAt; }
    public void setStartsAt(OffsetDateTime startsAt) { this.startsAt = startsAt; }
    public OffsetDateTime getEndsAt() { return endsAt; }
    public void setEndsAt(OffsetDateTime endsAt) { this.endsAt = endsAt; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }
    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
}