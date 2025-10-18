package com.maavooripachadi.pricing;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "tax_rule")
public class TaxRule extends BaseEntity {
    @Column(nullable = false, length = 64)
    private String regionPattern; // e.g., ".*" or "AP|TS" or "IN"
    @Column(nullable = false)
    private int ratePercent; // e.g., 5 => 5%
    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;


    public String getRegionPattern() { return regionPattern; }
    public void setRegionPattern(String regionPattern) { this.regionPattern = regionPattern; }
    public int getRatePercent() { return ratePercent; }
    public void setRatePercent(int ratePercent) { this.ratePercent = ratePercent; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}