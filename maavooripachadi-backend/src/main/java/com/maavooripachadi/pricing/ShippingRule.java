package com.maavooripachadi.pricing;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "shipping_rule")
public class ShippingRule extends BaseEntity {
    @Column(nullable = false)
    private String pincodePattern; // regex: e.g., "5.*" for AP/TS, ".*" for all
    @Column(nullable = false)
    private int minSubtotalPaise; // threshold to apply this rule
    @Column(nullable = false)
    private int feePaise; // shipping fee when matched
    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;


    public String getPincodePattern() { return pincodePattern; }
    public void setPincodePattern(String pincodePattern) { this.pincodePattern = pincodePattern; }
    public int getMinSubtotalPaise() { return minSubtotalPaise; }
    public void setMinSubtotalPaise(int minSubtotalPaise) { this.minSubtotalPaise = minSubtotalPaise; }
    public int getFeePaise() { return feePaise; }
    public void setFeePaise(int feePaise) { this.feePaise = feePaise; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}