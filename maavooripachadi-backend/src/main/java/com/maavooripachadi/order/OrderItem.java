package com.maavooripachadi.order;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "order_item")
public class OrderItem extends BaseEntity {


  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id")
  private Order order;


  @Column(nullable = false)
  private Long variantId;


  private String sku;
  private String title; // product title snapshot


  @Column(nullable = false)
  private int qty;


  @Column(nullable = false)
  private int unitPricePaise;


  @Column(nullable = false)
  private int lineTotalPaise;


  // getters/setters
  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }
  public Long getVariantId() { return variantId; }
  public void setVariantId(Long variantId) { this.variantId = variantId; }
  public String getSku() { return sku; }
  public void setSku(String sku) { this.sku = sku; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public int getQty() { return qty; }
  public void setQty(int qty) { this.qty = qty; }
  public int getUnitPricePaise() { return unitPricePaise; }
  public void setUnitPricePaise(int unitPricePaise) { this.unitPricePaise = unitPricePaise; }
  public int getLineTotalPaise() { return lineTotalPaise; }
  public void setLineTotalPaise(int lineTotalPaise) { this.lineTotalPaise = lineTotalPaise; }
}