package com.maavooripachadi.disputes;

import com.maavooripachadi.disputes.dto.CreateDisputeRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DisputeWebhookControllerTest {

  private DisputeService service;
  private DisputeWebhookController controller;
  private HttpServletRequest httpRequest;

  @BeforeEach
  void setUp() {
    service = mock(DisputeService.class);
    controller = new DisputeWebhookController(service);
    httpRequest = mock(HttpServletRequest.class);
  }

  @Test
  void ingestBuildsCreateRequestAndReturnsAcknowledgement() {
    Map<String, Object> payload = new HashMap<>();
    payload.put("case_id", "CASE-22");
    payload.put("order_no", "ORD-77");
    payload.put("amount_paise", 12345);
    payload.put("reason", "fraud");
    payload.put("type", "chargeback");

    Dispute dispute = new Dispute();
    ReflectionTestUtils.setField(dispute, "id", 555L);

    ArgumentCaptor<CreateDisputeRequest> requestCaptor = ArgumentCaptor.forClass(CreateDisputeRequest.class);
    when(service.create(requestCaptor.capture())).thenReturn(dispute);

    Map<String, Object> response = controller.ingest("razorpay", payload, httpRequest);

    assertThat(response).containsEntry("ok", true).containsEntry("disputeId", 555L);

    CreateDisputeRequest captured = requestCaptor.getValue();
    assertThat(captured.gateway()).isEqualTo("RAZORPAY");
    assertThat(captured.providerCaseId()).isEqualTo("CASE-22");
    assertThat(captured.orderNo()).isEqualTo("ORD-77");
    assertThat(captured.amountPaise()).isEqualTo(12345);
    assertThat(captured.reason()).isEqualTo("fraud");
    assertThat(captured.type()).isEqualTo("chargeback");
    assertThat(captured.currency()).isEqualTo("INR");
  }

  @Test
  void ingestFallsBackToGenericKeysWhenOptionalFieldsMissing() {
    Map<String, Object> payload = Map.of(
        "id", "ALT-1",
        "amount_paise", 5000
    );

    Dispute dispute = new Dispute();
    ReflectionTestUtils.setField(dispute, "id", 42L);
    when(service.create(any(CreateDisputeRequest.class))).thenReturn(dispute);

    Map<String, Object> response = controller.ingest("cashfree", payload, httpRequest);

    assertThat(response).containsEntry("disputeId", 42L);

    ArgumentCaptor<CreateDisputeRequest> requestCaptor = ArgumentCaptor.forClass(CreateDisputeRequest.class);
    verify(service).create(requestCaptor.capture());
    CreateDisputeRequest captured = requestCaptor.getValue();
    assertThat(captured.providerCaseId()).isEqualTo("ALT-1");
    assertThat(captured.reason()).isEqualTo("other");
    assertThat(captured.type()).isEqualTo("chargeback");
    assertThat(captured.orderNo()).isNull();
  }
}
