package com.maavooripachadi.cart;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "cart_item")
public class CartItem extends BaseEntity {
  @ManyToOne(optional = false)
  private Cart cart;


  @Column(nullable = false)
  private Long variantId;


  @Column(nullable = false)
  private int qty;


  @Column(nullable = false)
  private int unitPricePaise; // store paise to avoid rounding issues


  private String title; // optional: for quick display
  private String imageUrl; // optional


  public CartItem() {
  }


  public CartItem(Cart cart, Long variantId, int qty, int unitPricePaise, String title, String imageUrl) {
    this.cart = cart;
    this.variantId = variantId;
    this.qty = qty;
    this.unitPricePaise = unitPricePaise;
    this.title = title;
    this.imageUrl = imageUrl;
  }


  public Cart getCart() {
    return cart;
  }


  public void setCart(Cart cart) {
    this.cart = cart;
  }


  public Long getVariantId() {
    return variantId;
  }


  public void setVariantId(Long variantId) {
    this.variantId = variantId;
  }


  public int getQty() {
    return qty;
  }


  public void setQty(int qty) {
    this.qty = qty;
  }


  public int getUnitPricePaise() {
    return unitPricePaise;
  }


  public void setUnitPricePaise(int unitPricePaise) {
    this.unitPricePaise = unitPricePaise;
  }


  public String getTitle() {
    return title;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public String getImageUrl() {
    return imageUrl;
  }


  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
