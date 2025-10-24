package com.maavooripachadi.payments;

import com.maavooripachadi.payments.gateway.AttemptStatus;
import com.maavooripachadi.payments.gateway.PaymentAttempt;
import com.maavooripachadi.payments.gateway.PaymentAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentsWebhookControllerTest {

  private PaymentAttemptRepository attemptRepository;
  private PaymentsWebhookController controller;

  @BeforeEach
  void setUp() {
    attemptRepository = mock(PaymentAttemptRepository.class);
    controller = new PaymentsWebhookController(attemptRepository);
  }

  @Test
  void webhookUpdatesAttemptWhenFound() {
    PaymentAttempt attempt = new PaymentAttempt();
    attempt.setStatus(AttemptStatus.CREATED);

    when(attemptRepository.findByGatewayOrderId("gw-order")).thenReturn(Optional.of(attempt));
    when(attemptRepository.save(any(PaymentAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Map<String, Object> response = controller.webhook("razorpay", Map.of(
        "order_id", "gw-order",
        "payment_id", "pay-123"
    ));

    assertThat(response).containsEntry("ok", true);
    assertThat(attempt.getGatewayPaymentId()).isEqualTo("pay-123");
    assertThat(attempt.getStatus()).isEqualTo(AttemptStatus.CAPTURED);
    verify(attemptRepository).save(attempt);
  }

  @Test
  void webhookNoopsWhenAttemptMissing() {
    when(attemptRepository.findByGatewayOrderId("unknown")).thenReturn(Optional.empty());

    Map<String, Object> response = controller.webhook("razorpay", Map.of("order_id", "unknown"));

    assertThat(response).containsEntry("ok", true);
    verify(attemptRepository, never()).save(any());
  }
}
