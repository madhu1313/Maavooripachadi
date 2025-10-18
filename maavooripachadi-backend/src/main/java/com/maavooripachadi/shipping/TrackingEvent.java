package com.maavooripachadi.shipping;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tracking_event", indexes = @Index(name="ix_track_ship", columnList = "shipment_id"))
public class TrackingEvent extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    private String status; // e.g., PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED
    private String location;
    @Lob private String details;

    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
