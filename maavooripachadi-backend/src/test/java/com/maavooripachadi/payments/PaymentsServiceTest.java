package com.maavooripachadi.payments;

import com.maavooripachadi.order.Order;
import com.maavooripachadi.order.OrderService;
import com.maavooripachadi.order.PaymentStatus;
import com.maavooripachadi.payments.dto.CaptureRequest;
import com.maavooripachadi.payments.dto.CreateIntentRequest;
import com.maavooripachadi.payments.dto.CreateIntentResponse;
import com.maavooripachadi.payments.dto.RecoveryIssueRequest;
import com.maavooripachadi.payments.dto.RecoveryIssueResponse;
import com.maavooripachadi.payments.dto.RecoveryValidateResponse;
import com.maavooripachadi.payments.dto.RefundRequest;
import com.maavooripachadi.payments.gateway.AttemptStatus;
import com.maavooripachadi.payments.gateway.GatewayName;
import com.maavooripachadi.payments.gateway.PaymentAttempt;
import com.maavooripachadi.payments.gateway.PaymentAttemptRepository;
import com.maavooripachadi.payments.gateway.PaymentGateway;
import com.maavooripachadi.payments.gateway.PaymentRefund;
import com.maavooripachadi.payments.gateway.PaymentRefundRepository;
import com.maavooripachadi.payments.gateway.PaymentRouterService;
import com.maavooripachadi.payments.gateway.RefundStatus;
import com.maavooripachadi.payments.recovery.RecoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaymentsServiceTest {

  private PaymentAttemptRepository attemptRepository;
  private PaymentRefundRepository refundRepository;
  private PaymentRouterService routerService;
  private RecoveryService recoveryService;
  private OrderService orderService;
  private PaymentsService service;
  private PaymentGateway gateway;

  @BeforeEach
  void setUp() {
    attemptRepository = mock(PaymentAttemptRepository.class);
    refundRepository = mock(PaymentRefundRepository.class);
    routerService = mock(PaymentRouterService.class);
    recoveryService = mock(RecoveryService.class);
    orderService = mock(OrderService.class);
    gateway = mock(PaymentGateway.class);

    service = new PaymentsService(attemptRepository, refundRepository, routerService, recoveryService, orderService);
  }

  @Test
  void createIntentUsesOrderTotalsWhenAvailable() {
    Order order = new Order();
    order.setOrderNo("ORD-1");
    order.setTotalPaise(25_000);
    order.setCurrency("INR");
    order.setPaymentStatus(PaymentStatus.PENDING);

    when(orderService.getByOrderNo("ORD-1")).thenReturn(order);
    when(routerService.pick(GatewayName.RAZORPAY)).thenReturn(gateway);
    when(gateway.createGatewayOrder(any(PaymentAttempt.class))).thenReturn("gw-order-1");
    when(attemptRepository.save(any(PaymentAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CreateIntentRequest request = new CreateIntentRequest();
    request.setOrderNo("ORD-1");
    request.setAmountPaise(999); // ignored because order total prevails
    request.setCurrency("USD"); // ignored

    CreateIntentResponse response = service.createIntent(request);

    assertThat(response.getOrderNo()).isEqualTo("ORD-1");
    assertThat(response.getGatewayOrderId()).isEqualTo("gw-order-1");
    assertThat(response.getAmountPaise()).isEqualTo(25_000);
    assertThat(response.getCurrency()).isEqualTo("INR");

    ArgumentCaptor<PaymentAttempt> attemptCaptor = ArgumentCaptor.forClass(PaymentAttempt.class);
    verify(attemptRepository).save(attemptCaptor.capture());
    PaymentAttempt persisted = attemptCaptor.getValue();
    assertThat(persisted.getAmountPaise()).isEqualTo(25_000);
    assertThat(persisted.getStatus()).isEqualTo(AttemptStatus.CREATED);
    assertThat(persisted.getGatewayOrderId()).isEqualTo("gw-order-1");
    assertThat(persisted.getGateway()).isEqualTo(GatewayName.RAZORPAY);
  }

  @Test
  void createIntentThrowsWhenOrderAlreadyPaid() {
    Order order = new Order();
    order.setOrderNo("ORD-2");
    order.setTotalPaise(10_000);
    order.setPaymentStatus(PaymentStatus.CAPTURED);

    when(orderService.getByOrderNo("ORD-2")).thenReturn(order);

    CreateIntentRequest request = new CreateIntentRequest();
    request.setOrderNo("ORD-2");
    request.setAmountPaise(10_000);

    assertThatThrownBy(() -> service.createIntent(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("already paid");

    verifyNoInteractions(routerService);
  }

  @Test
  void createIntentFallsBackToClientAmountWhenOrderNotYetVisible() {
    when(orderService.getByOrderNo("ORD-3")).thenThrow(new NoSuchElementException("not yet ready"));
    when(routerService.pick(GatewayName.CASHFREE)).thenReturn(gateway);
    when(gateway.createGatewayOrder(any(PaymentAttempt.class))).thenReturn("cashfree-order");
    when(attemptRepository.save(any(PaymentAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CreateIntentRequest request = new CreateIntentRequest();
    request.setOrderNo("ORD-3");
    request.setGateway(GatewayName.CASHFREE);
    request.setAmountPaise(12_345);
    request.setCurrency("USD");

    CreateIntentResponse response = service.createIntent(request);

    assertThat(response.getGateway()).isEqualTo(GatewayName.CASHFREE.name());
    assertThat(response.getAmountPaise()).isEqualTo(12_345);
    assertThat(response.getCurrency()).isEqualTo("USD");
  }

  @Test
  void createIntentRejectsZeroAmount() {
    Order order = new Order();
    order.setOrderNo("ORD-4");
    order.setTotalPaise(0);

    when(orderService.getByOrderNo("ORD-4")).thenReturn(order);

    CreateIntentRequest request = new CreateIntentRequest();
    request.setOrderNo("ORD-4");
    request.setAmountPaise(0);

    assertThatThrownBy(() -> service.createIntent(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Invalid payment amount");
  }

  @Test
  void captureVerifiesSignatureAndMarksOrderPaid() {
    PaymentAttempt attempt = new PaymentAttempt();
    attempt.setOrderNo("ORD-5");
    attempt.setGateway(GatewayName.RAZORPAY);
    attempt.setGatewayOrderId("gw-order");

    when(attemptRepository.findFirstByOrderNoOrderByCreatedAtDesc("ORD-5")).thenReturn(Optional.of(attempt));
    when(routerService.pick(GatewayName.RAZORPAY)).thenReturn(gateway);
    when(gateway.verifySignature("gw-order|pay-1", "signature", null)).thenReturn(true);
    when(attemptRepository.save(any(PaymentAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(orderService.markPaid(eq("ORD-5"), anyString(), anyString())).thenReturn(new Order());

    CaptureRequest request = new CaptureRequest();
    request.setOrderNo("ORD-5");
    request.setGatewayPaymentId("pay-1");
    request.setGatewaySignature("signature");

    PaymentAttempt captured = service.capture(request);

    assertThat(captured.getGatewayPaymentId()).isEqualTo("pay-1");
    assertThat(captured.getGatewaySignature()).isEqualTo("signature");
    assertThat(captured.getStatus()).isEqualTo(AttemptStatus.CAPTURED);

    verify(orderService).markPaid("ORD-5", "RAZORPAY", "pay-1");
  }

  @Test
  void captureThrowsWhenSignatureInvalid() {
    PaymentAttempt attempt = new PaymentAttempt();
    attempt.setOrderNo("ORD-6");
    attempt.setGateway(GatewayName.RAZORPAY);
    attempt.setGatewayOrderId("gw-order");

    when(attemptRepository.findFirstByOrderNoOrderByCreatedAtDesc("ORD-6")).thenReturn(Optional.of(attempt));
    when(routerService.pick(GatewayName.RAZORPAY)).thenReturn(gateway);
    when(gateway.verifySignature(anyString(), anyString(), any())).thenReturn(false);

    CaptureRequest request = new CaptureRequest();
    request.setOrderNo("ORD-6");
    request.setGatewayPaymentId("pay-x");
    request.setGatewaySignature("bad");

    assertThatThrownBy(() -> service.capture(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Signature verification failed");

    verify(attemptRepository, never()).save(any());
    verify(orderService, never()).markPaid(anyString(), anyString(), anyString());
  }

  @Test
  void refundPersistsRefundRecord() {
    PaymentAttempt attempt = new PaymentAttempt();
    attempt.setGateway(GatewayName.RAZORPAY);
    attempt.setGatewayPaymentId("pay-1");

    when(attemptRepository.findFirstByOrderNoOrderByCreatedAtDesc("ORD-7")).thenReturn(Optional.of(attempt));
    when(routerService.pick(GatewayName.RAZORPAY)).thenReturn(gateway);
    when(gateway.refund("pay-1", 5_000, "Damaged")).thenReturn("refund-123");
    when(refundRepository.save(any(PaymentRefund.class))).thenAnswer(invocation -> invocation.getArgument(0));

    RefundRequest request = new RefundRequest();
    request.setOrderNo("ORD-7");
    request.setAmountPaise(5_000);
    request.setReason("Damaged");

    PaymentRefund refund = service.refund(request);

    assertThat(refund.getAmountPaise()).isEqualTo(5_000);
    assertThat(refund.getGatewayRefundId()).isEqualTo("refund-123");
    assertThat(refund.getStatus()).isEqualTo(RefundStatus.PROCESSED);
    assertThat(refund.getAttempt()).isSameAs(attempt);
  }

  @Test
  void recoveryHelpersDelegateToRecoveryService() {
    com.maavooripachadi.payments.recovery.dto.RecoveryIssueResponse subIssue = new com.maavooripachadi.payments.recovery.dto.RecoveryIssueResponse();
    subIssue.setToken("tok");
    subIssue.setUrl("https://url");
    when(recoveryService.issue(any())).thenReturn(subIssue);

    RecoveryIssueRequest request = new RecoveryIssueRequest();
    request.setOrderNo("ORD-8");

    RecoveryIssueResponse issueResponse = service.issueRecovery(request);
    assertThat(issueResponse.getToken()).isEqualTo("tok");
    assertThat(issueResponse.getUrl()).isEqualTo("https://url");

    com.maavooripachadi.payments.recovery.dto.RecoveryValidateResponse subValidate = new com.maavooripachadi.payments.recovery.dto.RecoveryValidateResponse();
    subValidate.setOk(true);
    subValidate.setOrderNo("ORD-9");
    when(recoveryService.validate("token")).thenReturn(subValidate);

    RecoveryValidateResponse validateResponse = service.validateRecovery("token");
    assertThat(validateResponse.isOk()).isTrue();
    assertThat(validateResponse.getOrderNo()).isEqualTo("ORD-9");

    service.consumeRecovery("token");
    verify(recoveryService).consume("token");
  }
}
