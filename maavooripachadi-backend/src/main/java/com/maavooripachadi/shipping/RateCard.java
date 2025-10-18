package com.maavooripachadi.shipping;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "ship_rate_card")
public class RateCard extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarrierCode carrier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceLevel service;

    @ManyToOne(optional = false)
    @JoinColumn(name = "zone_id")
    private ShippingZone zone;

    // base price in paise for first 500g; then slab per 500g
    private int basePaise;
    private int per500gPaise;

    public CarrierCode getCarrier() { return carrier; }
    public void setCarrier(CarrierCode carrier) { this.carrier = carrier; }
    public ServiceLevel getService() { return service; }
    public void setService(ServiceLevel service) { this.service = service; }
    public ShippingZone getZone() { return zone; }
    public void setZone(ShippingZone zone) { this.zone = zone; }
    public int getBasePaise() { return basePaise; }
    public void setBasePaise(int basePaise) { this.basePaise = basePaise; }
    public int getPer500gPaise() { return per500gPaise; }
    public void setPer500gPaise(int per500gPaise) { this.per500gPaise = per500gPaise; }
}
