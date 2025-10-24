package com.maavooripachadi.content;

import com.maavooripachadi.content.dto.SubmitContentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ContentAdminControllerTest {

  private ContentApprovalService approvalService;
  private ContentService contentService;
  private ContentAdminController controller;

  @BeforeEach
  void setUp() {
    approvalService = mock(ContentApprovalService.class);
    contentService = mock(ContentService.class);
    controller = new ContentAdminController(approvalService, contentService);
  }

  @Test
  void submitReturnsApprovalMetadataAndDefaultsActor() {
    ContentApproval approval = new ContentApproval();
    approval.setStatus("PENDING");
    ReflectionTestUtils.setField(approval, "id", 88L);

    when(approvalService.submit("blog", 12L, "system")).thenReturn(approval);

    Map<String, Object> response = controller.submit(new SubmitContentRequest("blog", "12"), null);

    assertThat(response).containsEntry("approvalId", 88L).containsEntry("status", "PENDING");
    verify(approvalService).submit("blog", 12L, "system");
  }

  @Test
  void decideUsesProvidedActorAndReturnsStatus() {
    ContentApproval approval = new ContentApproval();
    approval.setStatus("APPROVED");
    ReflectionTestUtils.setField(approval, "id", 77L);

    when(approvalService.decide(77L, "approved", "jane", "Looks good")).thenReturn(approval);

    Map<String, Object> response = controller.decide(77L, "approved", "Looks good", "jane");

    assertThat(response).containsEntry("approvalId", 77L).containsEntry("status", "APPROVED");
    verify(approvalService).decide(77L, "approved", "jane", "Looks good");
  }

  @Test
  void publishEndpointInvokesService() {
    Map<String, Object> response = controller.publish("recipe", 22L);

    assertThat(response).containsEntry("ok", true);
    verify(contentService).publish("recipe", 22L);
  }

  @Test
  void unpublishEndpointInvokesService() {
    Map<String, Object> response = controller.unpublish("blog", 65L);

    assertThat(response).containsEntry("ok", true);
    verify(contentService).unpublish("blog", 65L);
  }
}
