package com.maavooripachadi.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ContentApprovalControllerTest {

  private ContentApprovalService service;
  private ContentApprovalController controller;

  @BeforeEach
  void setUp() {
    service = mock(ContentApprovalService.class);
    controller = new ContentApprovalController(service);
  }

  @Test
  void submitEndpointStartsReviewAndReturnsStatus() {
    Map<String, Object> response = controller.submit("recipe", 41L);

    assertThat(response).containsEntry("status", "IN_REVIEW");
    verify(service).submit("recipe", 41L, "system");
  }

  @Test
  void decideEndpointDelegatesAndEchoesDecision() {
    Map<String, Object> response = controller.decide("blog", 55L, "approved");

    assertThat(response).containsEntry("status", "approved");
    verify(service).decide(55L, "approved", "admin", null);
  }
}
