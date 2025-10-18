package com.maavooripachadi.logistics;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "stock_movement")
public class StockMovement extends BaseEntity {
    @ManyToOne(optional = false)
    private Inventory inventory;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;


    @Column(nullable = false)
    private int quantity; // positive numbers


    private String reason; // optional human reason (e.g., orderNo)


    private String ref; // external reference (order/shipment id)


    // getters/setters
    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }
    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
}