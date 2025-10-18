package com.maavooripachadi.logistics;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "log_package_item")
public class PackageItem extends BaseEntity {
    @ManyToOne(optional = false)
    private Shipment shipment;


    @Column(nullable = false)
    private Long variantId;


    private String sku;


    @Column(nullable = false)
    private int qty;


    // getters/setters
    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}
