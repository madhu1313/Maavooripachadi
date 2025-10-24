package com.maavooripachadi.payments;

import com.maavooripachadi.payments.dto.RefundRequest;
import com.maavooripachadi.payments.gateway.PaymentAttempt;
import com.maavooripachadi.payments.gateway.PaymentAttemptRepository;
import com.maavooripachadi.payments.gateway.PaymentRefund;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PaymentsAdminControllerTest {

  private PaymentsService paymentsService;
  private PaymentAttemptRepository attemptRepository;
  private PaymentsAdminController controller;

  @BeforeEach
  void setUp() {
    paymentsService = mock(PaymentsService.class);
    attemptRepository = mock(PaymentAttemptRepository.class);
    controller = new PaymentsAdminController(paymentsService, attemptRepository);
  }

  @Test
  void getReturnsLatestAttempt() {
    PaymentAttempt attempt = new PaymentAttempt();
    when(attemptRepository.findFirstByOrderNoOrderByCreatedAtDesc("ORD-1")).thenReturn(Optional.of(attempt));

    Optional<PaymentAttempt> result = controller.get("ORD-1");

    assertThat(result).contains(attempt);
    verify(attemptRepository).findFirstByOrderNoOrderByCreatedAtDesc("ORD-1");
  }

  @Test
  void refundDelegatesToService() {
    RefundRequest request = new RefundRequest();
    request.setOrderNo("ORD-2");

    PaymentRefund refund = new PaymentRefund();
    when(paymentsService.refund(request)).thenReturn(refund);

    PaymentRefund result = controller.refund(request);

    assertThat(result).isSameAs(refund);
    verify(paymentsService).refund(request);
  }
}
