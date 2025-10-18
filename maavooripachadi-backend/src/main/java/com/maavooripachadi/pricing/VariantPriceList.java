package com.maavooripachadi.pricing;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class VariantPriceList extends BaseEntity {
  private Long variantId;
  private String listCode;
  private Integer priceMrp;
  private Integer priceSale;

  public VariantPriceList() {
  }

  public Long getVariantId() {
    return variantId;
  }

  public void setVariantId(Long variantId) {
    this.variantId = variantId;
  }

  public String getListCode() {
    return listCode;
  }

  public void setListCode(String listCode) {
    this.listCode = listCode;
  }

  public Integer getPriceMrp() {
    return priceMrp;
  }

  public void setPriceMrp(Integer priceMrp) {
    this.priceMrp = priceMrp;
  }

  public Integer getPriceSale() {
    return priceSale;
  }

  public void setPriceSale(Integer priceSale) {
    this.priceSale = priceSale;
  }
}
