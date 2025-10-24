package com.maavooripachadi.payments;

import com.maavooripachadi.payments.dto.CaptureRequest;
import com.maavooripachadi.payments.dto.CreateIntentRequest;
import com.maavooripachadi.payments.dto.CreateIntentResponse;
import com.maavooripachadi.payments.dto.RefundRequest;
import com.maavooripachadi.payments.gateway.PaymentAttempt;
import com.maavooripachadi.payments.gateway.PaymentRefund;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PaymentsControllerTest {

  private PaymentsService paymentsService;
  private PaymentsController controller;

  @BeforeEach
  void setUp() {
    paymentsService = mock(PaymentsService.class);
    controller = new PaymentsController(paymentsService);
  }

  @Test
  void intentEndpointDelegatesToService() {
    CreateIntentRequest request = new CreateIntentRequest();
    request.setOrderNo("ORD-1");
    request.setAmountPaise(1_000);

    CreateIntentResponse response = new CreateIntentResponse();
    response.setOrderNo("ORD-1");
    response.setGatewayOrderId("gw-1");

    when(paymentsService.createIntent(request)).thenReturn(response);

    CreateIntentResponse result = controller.intent(request);

    assertThat(result).isSameAs(response);
    verify(paymentsService).createIntent(request);
  }

  @Test
  void captureEndpointReturnsSavedAttempt() {
    CaptureRequest request = new CaptureRequest();
    request.setOrderNo("ORD-2");
    PaymentAttempt attempt = new PaymentAttempt();
    when(paymentsService.capture(request)).thenReturn(attempt);

    PaymentAttempt result = controller.capture(request);

    assertThat(result).isSameAs(attempt);
    verify(paymentsService).capture(request);
  }

  @Test
  void refundEndpointReturnsRefundRecord() {
    RefundRequest request = new RefundRequest();
    request.setOrderNo("ORD-3");
    PaymentRefund refund = new PaymentRefund();
    when(paymentsService.refund(request)).thenReturn(refund);

    PaymentRefund result = controller.refund(request);

    assertThat(result).isSameAs(refund);
    verify(paymentsService).refund(request);
  }
}
