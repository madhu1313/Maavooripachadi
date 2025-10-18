package com.maavooripachadi.shipping;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "ship_zone", indexes = @Index(name = "ix_zone_name", columnList = "name", unique = true))
public class ShippingZone extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name; // e.g., INDIA_MAINLAND, NORTH_EAST, REMOTE

    @Lob
    private String pincodesCsv; // comma separated pincode ranges or codes

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPincodesCsv() { return pincodesCsv; }
    public void setPincodesCsv(String pincodesCsv) { this.pincodesCsv = pincodesCsv; }
}
