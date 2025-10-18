package com.maavooripachadi.reviews;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "review_reply", indexes = @Index(name = "ix_review_reply_review", columnList = "review_id"))
public class ReviewReply extends BaseEntity {


    @ManyToOne(optional = false)
    @JoinColumn(name = "review_id")
    private Review review;


    @Column(nullable = false)
    private String author; // admin/staff name or id


    @Lob
    private String body;


    private Boolean publicVisible = Boolean.TRUE;


    // getters/setters
    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Boolean getPublicVisible() { return publicVisible; }
    public void setPublicVisible(Boolean publicVisible) { this.publicVisible = publicVisible; }
}