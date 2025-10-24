package com.maavooripachadi.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContentApprovalServiceTest {

  private ContentApprovalRepository repository;
  private ContentService contentService;
  private ContentApprovalService service;

  @BeforeEach
  void setUp() {
    repository = mock(ContentApprovalRepository.class);
    contentService = mock(ContentService.class);
    service = new ContentApprovalService(repository, contentService);
  }

  @Test
  void submitUppercasesTypeAndPersistsApproval() {
    when(repository.save(any(ContentApproval.class))).thenAnswer(invocation -> {
      ContentApproval toPersist = invocation.getArgument(0);
      ReflectionTestUtils.setField(toPersist, "id", 101L);
      return toPersist;
    });

    ContentApproval approval = service.submit("recipe", 42L, "alice");

    assertThat(approval.getId()).isEqualTo(101L);
    assertThat(approval.getType()).isEqualTo("RECIPE");
    assertThat(approval.getRefId()).isEqualTo(42L);
    assertThat(approval.getSubmittedBy()).isEqualTo("alice");
    assertThat(approval.getStatus()).isEqualTo("PENDING");
    verify(repository).save(any(ContentApproval.class));
  }

  @Test
  void decideWhenApprovedPublishesContent() {
    ContentApproval approval = new ContentApproval();
    approval.setType("BLOG");
    approval.setRefId(7L);
    approval.setStatus("PENDING");
    when(repository.findById(55L)).thenReturn(Optional.of(approval));
    when(repository.save(any(ContentApproval.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ContentApproval decided = service.decide(55L, "approved", "bob", "Looks good");

    assertThat(decided.getStatus()).isEqualTo("APPROVED");
    assertThat(decided.getDecidedBy()).isEqualTo("bob");
    assertThat(decided.getNote()).isEqualTo("Looks good");
    verify(contentService).publish("BLOG", 7L);
    verify(contentService, never()).unpublish(anyString(), anyLong());
    verify(repository).save(approval);
  }

  @Test
  void decideWhenRejectedUnpublishesContent() {
    ContentApproval approval = new ContentApproval();
    approval.setType("RECIPE");
    approval.setRefId(9L);
    when(repository.findById(66L)).thenReturn(Optional.of(approval));
    when(repository.save(any(ContentApproval.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ContentApproval decided = service.decide(66L, "rejected", "admin", "Needs rewrite");

    assertThat(decided.getStatus()).isEqualTo("REJECTED");
    assertThat(decided.getDecidedBy()).isEqualTo("admin");
    assertThat(decided.getNote()).isEqualTo("Needs rewrite");
    verify(contentService).unpublish("RECIPE", 9L);
    verify(contentService, never()).publish(anyString(), anyLong());
  }

  @Test
  void decideThrowsWhenApprovalMissing() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.decide(999L, "approved", "system", null))
        .isInstanceOf(java.util.NoSuchElementException.class);

    verify(repository, never()).save(any());
    verifyNoInteractions(contentService);
  }
}
