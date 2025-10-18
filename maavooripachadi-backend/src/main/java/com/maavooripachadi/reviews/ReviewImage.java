package com.maavooripachadi.reviews;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "review_image", indexes = @Index(name = "ix_review_image_review", columnList = "review_id"))
public class ReviewImage extends BaseEntity {


    @ManyToOne(optional = false)
    @JoinColumn(name = "review_id")
    private Review review;


    @Lob
    private String url; // stored path or CDN url


    private String altText;


    // getters/setters
    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }
}