package com.maavooripachadi.reviews;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "review_vote", uniqueConstraints = @UniqueConstraint(name = "uq_review_vote", columnNames = {"review_id","subject_id"}))
public class ReviewVote extends BaseEntity {


    @ManyToOne(optional = false)
    @JoinColumn(name = "review_id")
    private Review review;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType type = VoteType.HELPFUL;


    @Column(name = "subject_id", nullable = false)
    private String subjectId; // user id/email


    // getters/setters
    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }
    public VoteType getType() { return type; }
    public void setType(VoteType type) { this.type = type; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
}