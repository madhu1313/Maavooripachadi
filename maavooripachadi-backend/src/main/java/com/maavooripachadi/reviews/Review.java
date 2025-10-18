package com.maavooripachadi.reviews;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "review", indexes = {
        @Index(name = "ix_review_product", columnList = "product_id"),
        @Index(name = "ix_review_variant", columnList = "variant_id"),
        @Index(name = "ix_review_status", columnList = "status")
})
public class Review extends BaseEntity {
@Column(name = "product_id", nullable = false)
private Long productId;


@Column(name = "variant_id")
private Long variantId; // nullable


@Column(nullable = false)
private int rating; // 1..5


@Column(length = 120)
private String title;


@Lob
private String body;


@Column(name = "subject_id", length = 128)
private String subjectId; // user id/email


@Column(name = "verified_purchase")
private Boolean verifiedPurchase = Boolean.FALSE;


@Enumerated(EnumType.STRING)
@Column(nullable = false)
private ReviewStatus status = ReviewStatus.PENDING;


private int helpfulCount = 0;
private int notHelpfulCount = 0;


// getters/setters
public Long getProductId() { return productId; }
public void setProductId(Long productId) { this.productId = productId; }
public Long getVariantId() { return variantId; }
public void setVariantId(Long variantId) { this.variantId = variantId; }
public int getRating() { return rating; }
public void setRating(int rating) { this.rating = rating; }
public String getTitle() { return title; }
public void setTitle(String title) { this.title = title; }
public String getBody() { return body; }
public void setBody(String body) { this.body = body; }
public String getSubjectId() { return subjectId; }
public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
public Boolean getVerifiedPurchase() { return verifiedPurchase; }
public void setVerifiedPurchase(Boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
public ReviewStatus getStatus() { return status; }
public void setStatus(ReviewStatus status) { this.status = status; }
public int getHelpfulCount() { return helpfulCount; }
public void setHelpfulCount(int helpfulCount) { this.helpfulCount = helpfulCount; }
public int getNotHelpfulCount() { return notHelpfulCount; }
public void setNotHelpfulCount(int notHelpfulCount) { this.notHelpfulCount = notHelpfulCount; }
}