package com.maavooripachadi.catalog;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "product")
public class Product extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String slug;
  @Column(nullable = false)
  private String title;


  @Lob
  private String descriptionHtml;
  private String heroImageUrl;


  // Pricing (store in paise)
  @Column(nullable = false)
  private int pricePaise;
  private Integer mrpPaise;


  // Search & taxonomy
  private String categorySlug; // e.g., "spreads", "pickles"
  private String tags; // comma-separated tags
  @Lob
  private String searchText; // denormalized searchable copy


  // Flags
  private boolean inStock = true;
  private String badge; // e.g., "Bestseller"


  public Product() {
  }


  public String getSlug() {
    return slug;
  }


  public void setSlug(String slug) {
    this.slug = slug;
  }


  public String getTitle() {
    return title;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public String getDescriptionHtml() {
    return descriptionHtml;
  }


  public void setDescriptionHtml(String descriptionHtml) {
    this.descriptionHtml = descriptionHtml;
  }


  public String getHeroImageUrl() {
    return heroImageUrl;
  }


  public void setHeroImageUrl(String heroImageUrl) {
    this.heroImageUrl = heroImageUrl;
  }


  public int getPricePaise() {
    return pricePaise;
  }


  public void setPricePaise(int pricePaise) {
    this.pricePaise = pricePaise;
  }


  public Integer getMrpPaise() {
    return mrpPaise;
  }


  public void setMrpPaise(Integer mrpPaise) {
    this.mrpPaise = mrpPaise;
  }


  public String getCategorySlug() {
    return categorySlug;
  }


  public void setCategorySlug(String categorySlug) {
    this.categorySlug = categorySlug;
  }


  public String getTags() {
    return tags;
  }


  public void setTags(String tags) {
    this.tags = tags;
  }


  public String getSearchText() {
    return searchText;
  }


  public void setSearchText(String searchText) {
    this.searchText = searchText;
  }


  public boolean isInStock() {
    return inStock;
  }


  public void setInStock(boolean inStock) {
    this.inStock = inStock;
  }


  public String getBadge() {
    return badge;
  }


  public void setBadge(String badge) {
    this.badge = badge;
  }
}
