package com.maavooripachadi.disputes;

import com.maavooripachadi.disputes.dto.CreateDisputeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DisputeServiceTest {

  private DisputeRepository disputes;
  private DisputeEventRepository events;
  private DisputeService service;

  @BeforeEach
  void setUp() {
    disputes = mock(DisputeRepository.class);
    events = mock(DisputeEventRepository.class);
    service = new DisputeService(disputes, events);
  }

  @Test
  void createReturnsExistingDisputeWhenProviderCaseMatches() {
    Dispute existing = new Dispute();
    ReflectionTestUtils.setField(existing, "id", 101L);

    when(disputes.findByProviderCaseId("CASE-1")).thenReturn(Optional.of(existing));

    Dispute result = service.create(new CreateDisputeRequest("razorpay", "CASE-1", "ORD-1", null, "fraud", "chargeback", 5000, "inr"));

    assertThat(result).isSameAs(existing);
    verify(disputes, never()).save(any());
    verify(events, never()).save(any());
  }

  @Test
  void createPersistsDisputeAndOpeningEvent() {
    when(disputes.findByProviderCaseId("CASE-2")).thenReturn(Optional.empty());
    when(disputes.save(any(Dispute.class))).thenAnswer(invocation -> {
      Dispute toPersist = invocation.getArgument(0);
      ReflectionTestUtils.setField(toPersist, "id", 202L);
      return toPersist;
    });
    when(events.save(any(DisputeEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CreateDisputeRequest request = new CreateDisputeRequest("cashfree", "CASE-2", "ORD-9", 88L, "not_received", "chargeback", 9000, null);

    Dispute saved = service.create(request);

    assertThat(saved.getId()).isEqualTo(202L);
    assertThat(saved.getGateway()).isEqualTo("CASHFREE");
    assertThat(saved.getCurrency()).isEqualTo("INR");
    assertThat(saved.getStatus()).isEqualTo(DisputeStatus.OPEN);
    assertThat(saved.getOrderNo()).isEqualTo("ORD-9");
    assertThat(saved.getPaymentAttemptId()).isEqualTo(88L);

    ArgumentCaptor<DisputeEvent> eventCaptor = ArgumentCaptor.forClass(DisputeEvent.class);
    verify(events).save(eventCaptor.capture());
    DisputeEvent event = eventCaptor.getValue();
    assertThat(event.getDispute()).isSameAs(saved);
    assertThat(event.getType()).isEqualTo(DisputeEventType.OPENED);
    assertThat(event.getPayload()).isEqualTo("dispute opened");
  }

  @Test
  void updateStatusPersistsDecisionTimestampAndStatusChangeEvent() {
    Dispute stored = new Dispute();
    ReflectionTestUtils.setField(stored, "id", 77L);
    stored.setStatus(DisputeStatus.OPEN);

    when(disputes.findById(77L)).thenReturn(Optional.of(stored));
    when(disputes.save(any(Dispute.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Dispute updated = service.updateStatus(77L, DisputeStatus.WON, "issuer accepted evidence");

    assertThat(updated.getStatus()).isEqualTo(DisputeStatus.WON);
    assertThat(updated.getDecidedAt()).isNotNull();

    ArgumentCaptor<DisputeEvent> eventCaptor = ArgumentCaptor.forClass(DisputeEvent.class);
    verify(events).save(eventCaptor.capture());
    DisputeEvent event = eventCaptor.getValue();
    assertThat(event.getDispute()).isSameAs(stored);
    assertThat(event.getType()).isEqualTo(DisputeEventType.STATUS_CHANGE);
    assertThat(event.getPayload()).isEqualTo("issuer accepted evidence");
    verify(disputes).save(stored);
  }

  @Test
  void updateStatusWithoutNoteSkipsEventCreation() {
    Dispute stored = new Dispute();
    when(disputes.findById(55L)).thenReturn(Optional.of(stored));
    when(disputes.save(any(Dispute.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Dispute updated = service.updateStatus(55L, DisputeStatus.CLOSED, "  ");

    assertThat(updated.getStatus()).isEqualTo(DisputeStatus.CLOSED);
    assertThat(updated.getDecidedAt()).isNotNull();
    verify(events, never()).save(any());
  }

  @Test
  void updateStatusThrowsWhenDisputeMissing() {
    when(disputes.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateStatus(999L, DisputeStatus.LOST, "missing"))
        .isInstanceOf(java.util.NoSuchElementException.class);
  }

  @Test
  void addNoteAppendsAndPersistsEvent() {
    Dispute stored = new Dispute();
    stored.setNotes("old");
    when(disputes.findById(17L)).thenReturn(Optional.of(stored));
    when(disputes.save(any(Dispute.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Dispute result = service.addNote(17L, "new");

    assertThat(result.getNotes()).isEqualTo("old\nnew");
    ArgumentCaptor<DisputeEvent> eventCaptor = ArgumentCaptor.forClass(DisputeEvent.class);
    verify(events).save(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getType()).isEqualTo(DisputeEventType.NOTE);
    assertThat(eventCaptor.getValue().getPayload()).isEqualTo("new");
  }

  @Test
  void listUsesDescendingCreatedAtSort() {
    Page<Dispute> page = new PageImpl<>(List.of(new Dispute()));
    when(disputes.search(eq("RAZORPAY"), eq(DisputeStatus.OPEN), eq("ORD"), any(Pageable.class))).thenReturn(page);

    Page<Dispute> result = service.list("RAZORPAY", DisputeStatus.OPEN, "ORD", 1, 25);

    assertThat(result).isSameAs(page);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(disputes).search(eq("RAZORPAY"), eq(DisputeStatus.OPEN), eq("ORD"), pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isEqualTo(1);
    assertThat(pageable.getPageSize()).isEqualTo(25);
    assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
    assertThat(pageable.getSort().getOrderFor("createdAt").isDescending()).isTrue();
  }

  @Test
  void timelineFetchesChronologicalEvents() {
    DisputeEvent event = new DisputeEvent();
    ReflectionTestUtils.setField(event, "createdAt", OffsetDateTime.now());
    when(events.findByDisputeIdOrderByCreatedAtAsc(12L)).thenReturn(List.of(event));

    List<DisputeEvent> timeline = service.timeline(12L);

    assertThat(timeline).containsExactly(event);
    verify(events).findByDisputeIdOrderByCreatedAtAsc(12L);
  }
}
