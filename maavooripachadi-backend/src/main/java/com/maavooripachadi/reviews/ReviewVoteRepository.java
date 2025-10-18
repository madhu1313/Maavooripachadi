package com.maavooripachadi.reviews;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {
    Optional<ReviewVote> findByReviewIdAndSubjectId(Long reviewId, String subjectId);
}