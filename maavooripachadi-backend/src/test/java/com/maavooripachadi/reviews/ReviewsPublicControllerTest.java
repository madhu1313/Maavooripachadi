package com.maavooripachadi.reviews;

import com.maavooripachadi.reviews.dto.FlagRequest;
import com.maavooripachadi.reviews.dto.SubmitReviewRequest;
import com.maavooripachadi.reviews.dto.VoteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReviewsPublicControllerTest {

  private ReviewsService service;
  private ReviewsPublicController controller;

  @BeforeEach
  void setUp() {
    service = mock(ReviewsService.class);
    controller = new ReviewsPublicController(service);
  }

  @Test
  void submitDelegatesToService() {
    SubmitReviewRequest request = new SubmitReviewRequest();
    Review review = new Review();
    when(service.submit(request)).thenReturn(review);

    Review response = controller.submit(request);

    assertThat(response).isSameAs(review);
    verify(service).submit(request);
  }

  @Test
  void listDelegatesToService() {
    Page<Review> page = new PageImpl<>(List.of(new Review()));
    when(service.listPublic(10L, 0, 20)).thenReturn(page);

    Page<Review> response = controller.list(10L, 0, 20);

    assertThat(response).isSameAs(page);
    verify(service).listPublic(10L, 0, 20);
  }

  @Test
  void voteDelegatesToService() {
    VoteRequest request = new VoteRequest();
    Review review = new Review();
    when(service.vote(request)).thenReturn(review);

    Review response = controller.vote(request);

    assertThat(response).isSameAs(review);
    verify(service).vote(request);
  }

  @Test
  void flagDelegatesToService() {
    FlagRequest request = new FlagRequest();
    ReviewFlag flag = new ReviewFlag();
    when(service.flag(request)).thenReturn(flag);

    ReviewFlag response = controller.flag(request);

    assertThat(response).isSameAs(flag);
    verify(service).flag(request);
  }
}
