package com.maavooripachadi.catalog;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "variant")
public class Variant extends BaseEntity {
  @ManyToOne(optional = false)
  private Product product;

  @Column(unique = true, nullable = false)
  private String sku;

  private String label; // e.g., "250g", "500g"
  private int pricePaise;

  @Column(name = "in_stock")
  private boolean inStock = true;

  public Variant() {
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getPricePaise() {
    return pricePaise;
  }

  public void setPricePaise(int pricePaise) {
    this.pricePaise = pricePaise;
  }

  public boolean isInStock() {
    return inStock;
  }

  public void setInStock(boolean inStock) {
    this.inStock = inStock;
  }
}
