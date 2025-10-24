package com.maavooripachadi.disputes;

import com.maavooripachadi.disputes.dto.AddDisputeNoteRequest;
import com.maavooripachadi.disputes.dto.CreateDisputeRequest;
import com.maavooripachadi.disputes.dto.DisputeResponse;
import com.maavooripachadi.disputes.dto.UpdateDisputeStatusRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DisputeAdminControllerTest {

  private DisputeService service;
  private DisputeAdminController controller;

  @BeforeEach
  void setUp() {
    service = mock(DisputeService.class);
    controller = new DisputeAdminController(service);
  }

  @Test
  void createEndpointReturnsResponseDto() {
    CreateDisputeRequest request = new CreateDisputeRequest("razorpay", "CASE-1", "ORD-5", 10L, "fraud", "chargeback", 5000, "INR");
    Dispute dispute = buildDispute();

    when(service.create(request)).thenReturn(dispute);

    DisputeResponse response = controller.create(request);

    assertThat(response.id()).isEqualTo(42L);
    assertThat(response.gateway()).isEqualTo("RAZORPAY");
    verify(service).create(request);
  }

  @Test
  void updateStatusEndpointDelegatesToService() {
    UpdateDisputeStatusRequest request = new UpdateDisputeStatusRequest(DisputeStatus.WON, "note");
    Dispute dispute = buildDispute();
    dispute.setStatus(DisputeStatus.WON);

    when(service.updateStatus(42L, DisputeStatus.WON, "note")).thenReturn(dispute);

    DisputeResponse response = controller.updateStatus(42L, request);

    assertThat(response.status()).isEqualTo(DisputeStatus.WON);
    verify(service).updateStatus(42L, DisputeStatus.WON, "note");
  }

  @Test
  void addNoteEndpointReturnsUpdatedDetails() {
    Dispute dispute = buildDispute();
    dispute.setNotes("new note");
    when(service.addNote(42L, "new note")).thenReturn(dispute);

    DisputeResponse response = controller.addNote(42L, new AddDisputeNoteRequest("new note"));

    assertThat(response.notes()).isEqualTo("new note");
    verify(service).addNote(42L, "new note");
  }

  @Test
  void listEndpointUppercasesGatewayAndMapsResponses() {
    Dispute dispute = buildDispute();
    Page<Dispute> page = new PageImpl<>(List.of(dispute));
    when(service.list("RAZORPAY", DisputeStatus.OPEN, "ORD", 0, 20)).thenReturn(page);

    Page<DisputeResponse> response = controller.list("razorpay", DisputeStatus.OPEN, "ORD", 0, 20);

    assertThat(response.getContent()).extracting(DisputeResponse::id).containsExactly(42L);
    verify(service).list("RAZORPAY", DisputeStatus.OPEN, "ORD", 0, 20);
  }

  @Test
  void timelineEndpointTransformsEvents() {
    DisputeEvent event = new DisputeEvent();
    event.setType(DisputeEventType.NOTE);
    event.setPayload("payload");
    ReflectionTestUtils.setField(event, "id", 9L);
    when(service.timeline(42L)).thenReturn(List.of(event));

    var timeline = controller.timeline(42L);

    assertThat(timeline).hasSize(1);
    assertThat(timeline.get(0).payload()).isEqualTo("payload");
    verify(service).timeline(42L);
  }

  private Dispute buildDispute() {
    Dispute dispute = new Dispute();
    dispute.setGateway("RAZORPAY");
    dispute.setProviderCaseId("CASE-1");
    dispute.setOrderNo("ORD-5");
    dispute.setPaymentAttemptId(10L);
    dispute.setStatus(DisputeStatus.OPEN);
    dispute.setAmountPaise(5000);
    dispute.setCurrency("INR");
    dispute.setReason("fraud");
    dispute.setType("chargeback");
    dispute.setDecidedAt(OffsetDateTime.now());
    dispute.setNotes("note");
    ReflectionTestUtils.setField(dispute, "id", 42L);
    return dispute;
  }
}
