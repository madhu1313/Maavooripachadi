package com.maavooripachadi.reviews;

import com.maavooripachadi.reviews.dto.FlagRequest;
import com.maavooripachadi.reviews.dto.ReplyRequest;
import com.maavooripachadi.reviews.dto.SubmitReviewRequest;
import com.maavooripachadi.reviews.dto.VoteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReviewsServiceTest {

  private ReviewRepository reviews;
  private ReviewImageRepository images;
  private ReviewVoteRepository votes;
  private ReviewFlagRepository flags;
  private ReviewReplyRepository replies;
  private ProductRatingAggRepository aggregates;
  private OrderPort orders;
  private MediaPort media;
  private ReviewsService service;

  @BeforeEach
  void setUp() {
    reviews = mock(ReviewRepository.class);
    images = mock(ReviewImageRepository.class);
    votes = mock(ReviewVoteRepository.class);
    flags = mock(ReviewFlagRepository.class);
    replies = mock(ReviewReplyRepository.class);
    aggregates = mock(ProductRatingAggRepository.class);
    orders = mock(OrderPort.class);
    media = mock(MediaPort.class);
    service = new ReviewsService(reviews, images, votes, flags, replies, aggregates, orders, media);
  }

  @Test
  void submitCreatesVerifiedReviewAndPersistsAcceptableImages() {
    SubmitReviewRequest request = new SubmitReviewRequest();
    request.setProductId(101L);
    request.setVariantId(202L);
    request.setRating(5);
    request.setTitle("Great taste");
    request.setBody("Loved it");
    request.setSubjectId("user-1");
    request.setImageUrls(List.of("https://cdn/ok.jpg", "https://cdn/reject.jpg"));

    when(orders.hasPurchased("user-1", 101L, 202L)).thenReturn(true);
    when(media.isAcceptable("https://cdn/ok.jpg")).thenReturn(true);
    when(media.isAcceptable("https://cdn/reject.jpg")).thenReturn(false);
    when(reviews.save(any(Review.class))).thenAnswer(invocation -> {
      Review persisted = invocation.getArgument(0);
      ReflectionTestUtils.setField(persisted, "id", 55L);
      return persisted;
    });
    when(images.save(any(ReviewImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Review saved = service.submit(request);

    assertThat(saved.getVerifiedPurchase()).isTrue();
    assertThat(saved.getStatus()).isEqualTo(ReviewStatus.PENDING);
    verify(reviews).save(saved);
    verify(media).isAcceptable("https://cdn/ok.jpg");
    verify(media).isAcceptable("https://cdn/reject.jpg");

    ArgumentCaptor<ReviewImage> imageCaptor = ArgumentCaptor.forClass(ReviewImage.class);
    verify(images, times(1)).save(imageCaptor.capture());
    assertThat(imageCaptor.getValue().getUrl()).isEqualTo("https://cdn/ok.jpg");
  }

  @Test
  void listPublicDelegatesToRepository() {
    Page<Review> expected = new PageImpl<>(List.of(new Review()));
    when(reviews.findByProductIdAndStatusOrderByCreatedAtDesc(10L, ReviewStatus.APPROVED, PageRequest.of(0, 5)))
        .thenReturn(expected);

    Page<Review> result = service.listPublic(10L, 0, 5);

    assertThat(result).isSameAs(expected);
  }

  @Test
  void voteAddsHelpfulVoteWhenNoneExists() {
    Review review = new Review();
    review.setHelpfulCount(1);
    review.setNotHelpfulCount(0);

    when(reviews.findById(77L)).thenReturn(Optional.of(review));
    when(votes.findByReviewIdAndSubjectId(77L, "userA")).thenReturn(Optional.empty());
    when(votes.save(any(ReviewVote.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(reviews.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

    VoteRequest request = new VoteRequest();
    request.setReviewId(77L);
    request.setSubjectId("userA");
    request.setHelpful(true);

    Review updated = service.vote(request);

    assertThat(updated.getHelpfulCount()).isEqualTo(2);
    verify(votes).save(argThat(vote ->
        vote.getType() == VoteType.HELPFUL &&
            "userA".equals(vote.getSubjectId()) &&
            vote.getReview() == review
    ));
    verify(reviews).save(review);
  }

  @Test
  void voteSkipsWhenVoteAlreadyExists() {
    Review review = new Review();
    review.setHelpfulCount(3);
    ReviewVote existingVote = new ReviewVote();

    when(reviews.findById(88L)).thenReturn(Optional.of(review));
    when(votes.findByReviewIdAndSubjectId(88L, "userA")).thenReturn(Optional.of(existingVote));

    VoteRequest request = new VoteRequest();
    request.setReviewId(88L);
    request.setSubjectId("userA");
    request.setHelpful(false);

    Review result = service.vote(request);

    assertThat(result.getHelpfulCount()).isEqualTo(3);
    verify(votes, never()).save(any());
    verify(reviews, never()).save(any());
  }

  @Test
  void flagPersistsReviewFlag() {
    Review review = new Review();
    when(reviews.findById(91L)).thenReturn(Optional.of(review));
    when(flags.save(any(ReviewFlag.class))).thenAnswer(invocation -> invocation.getArgument(0));

    FlagRequest request = new FlagRequest();
    request.setReviewId(91L);
    request.setSubjectId("userFlagger");
    request.setReason("abuse");
    request.setDetails("contains inappropriate language");

    ReviewFlag flag = service.flag(request);

    assertThat(flag.getReason()).isEqualTo("abuse");
    assertThat(flag.getSubjectId()).isEqualTo("userFlagger");
    assertThat(flag.getReview()).isSameAs(review);
    verify(flags).save(flag);
  }

  @Test
  void replyDefaultsPublicVisibilityWhenNotProvided() {
    Review review = new Review();
    when(reviews.findById(32L)).thenReturn(Optional.of(review));
    when(replies.save(any(ReviewReply.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ReplyRequest request = new ReplyRequest();
    request.setReviewId(32L);
    request.setAuthor("Moderator");
    request.setBody("Thanks for the feedback!");
    request.setPublicVisible(null);

    ReviewReply reply = service.reply(request);

    assertThat(reply.getReview()).isSameAs(review);
    assertThat(reply.getAuthor()).isEqualTo("Moderator");
    assertThat(reply.getBody()).isEqualTo("Thanks for the feedback!");
    assertThat(reply.getPublicVisible()).isTrue();
    verify(replies).save(reply);
  }

  @Test
  void moderateApprovesReviewAndRecalculatesAggregates() {
    Review review = new Review();
    review.setProductId(500L);
    review.setVariantId(600L);
    review.setRating(5);
    review.setStatus(ReviewStatus.PENDING);

    Review otherApproved = new Review();
    otherApproved.setProductId(500L);
    otherApproved.setVariantId(600L);
    otherApproved.setRating(3);
    otherApproved.setStatus(ReviewStatus.APPROVED);

    when(reviews.findById(123L)).thenReturn(Optional.of(review));
    when(reviews.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(reviews.findAll()).thenReturn(List.of(review, otherApproved));
    when(aggregates.findByProductIdAndVariantId(500L, 600L)).thenReturn(Optional.empty());
    when(aggregates.save(any(ProductRatingAgg.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Review moderated = service.moderate(123L, ReviewStatus.APPROVED);

    assertThat(moderated.getStatus()).isEqualTo(ReviewStatus.APPROVED);

    ArgumentCaptor<ProductRatingAgg> aggCaptor = ArgumentCaptor.forClass(ProductRatingAgg.class);
    verify(aggregates).save(aggCaptor.capture());
    ProductRatingAgg agg = aggCaptor.getValue();
    assertThat(agg.getProductId()).isEqualTo(500L);
    assertThat(agg.getVariantId()).isEqualTo(600L);
    assertThat(agg.getCountReviews()).isEqualTo(2);
    assertThat(agg.getAvgRating()).isEqualTo(4.0);
  }
}
