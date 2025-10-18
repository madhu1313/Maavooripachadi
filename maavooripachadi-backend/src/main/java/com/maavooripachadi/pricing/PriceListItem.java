package com.maavooripachadi.pricing;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "price_list_item", uniqueConstraints = @UniqueConstraint(name = "uq_pl_variant", columnNames = {"price_list_id", "variant_id"}))
public class PriceListItem extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;


    @Column(name = "variant_id", nullable = false)
    private Long variantId;


    @Column(name = "price_paise", nullable = false)
    private int pricePaise;


    public PriceList getPriceList() { return priceList; }
    public void setPriceList(PriceList priceList) { this.priceList = priceList; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public int getPricePaise() { return pricePaise; }
    public void setPricePaise(int pricePaise) { this.pricePaise = pricePaise; }
}