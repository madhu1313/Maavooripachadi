package com.maavooripachadi.reviews.dto;


import jakarta.validation.constraints.*;
import java.util.List;


public class SubmitReviewRequest {
    @NotNull private Long productId;
    private Long variantId;
    @Min(1) @Max(5) private int rating;
    @Size(max=120) private String title;
    @Size(max=4000) private String body;
    @NotBlank private String subjectId;
    private List<String> imageUrls; // optional pre-uploaded urls


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
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}