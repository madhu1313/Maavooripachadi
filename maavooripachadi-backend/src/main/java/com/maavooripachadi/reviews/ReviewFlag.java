package com.maavooripachadi.reviews;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "review_flag")
public class ReviewFlag extends BaseEntity {


    @ManyToOne(optional = false)
    @JoinColumn(name = "review_id")
    private Review review;


    private String subjectId; // who flagged


    @Column(length = 64)
    private String reason; // ABUSE, SPAM, OFFENSIVE, OTHER


    @Lob
    private String details;


    // getters/setters
    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}