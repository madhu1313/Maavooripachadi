package com.maavooripachadi.reviews;

import com.maavooripachadi.reviews.dto.ReplyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReviewsAdminControllerTest {

  private ReviewsService service;
  private ReviewsAdminController controller;

  @BeforeEach
  void setUp() {
    service = mock(ReviewsService.class);
    controller = new ReviewsAdminController(service);
  }

  @Test
  void setStatusDelegatesToService() {
    Review review = new Review();
    when(service.moderate(44L, ReviewStatus.APPROVED)).thenReturn(review);

    Review response = controller.setStatus(44L, ReviewStatus.APPROVED);

    assertThat(response).isSameAs(review);
    verify(service).moderate(44L, ReviewStatus.APPROVED);
  }

  @Test
  void replyDelegatesToService() {
    ReplyRequest request = new ReplyRequest();
    ReviewReply reply = new ReviewReply();
    when(service.reply(request)).thenReturn(reply);

    ReviewReply response = controller.reply(request);

    assertThat(response).isSameAs(reply);
    verify(service).reply(request);
  }
}
