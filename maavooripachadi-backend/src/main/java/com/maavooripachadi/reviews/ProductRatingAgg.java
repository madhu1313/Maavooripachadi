package com.maavooripachadi.reviews;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "product_rating_agg", uniqueConstraints = @UniqueConstraint(name = "uq_rating_product_variant", columnNames = {"product_id","variant_id"}))
public class ProductRatingAgg extends BaseEntity {


    @Column(name = "product_id", nullable = false)
    private Long productId;


    @Column(name = "variant_id")
    private Long variantId; // nullable = overall product


    @Column(nullable = false)
    private int countReviews = 0;


    @Column(nullable = false)
    private double avgRating = 0.0; // 1..5


    // getters/setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public int getCountReviews() { return countReviews; }
    public void setCountReviews(int countReviews) { this.countReviews = countReviews; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
}