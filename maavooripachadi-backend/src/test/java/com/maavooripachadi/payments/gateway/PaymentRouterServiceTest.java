package com.maavooripachadi.payments.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PaymentRouterServiceTest {

  private RazorpayGateway razorpayGateway;
  private CashfreeGateway cashfreeGateway;
  private PaymentRouterService routerService;

  @BeforeEach
  void setUp() {
    razorpayGateway = mock(RazorpayGateway.class);
    cashfreeGateway = mock(CashfreeGateway.class);
    routerService = new PaymentRouterService(razorpayGateway, cashfreeGateway);
  }

  @Test
  void pickReturnsRazorpayWhenRequested() {
    assertThat(routerService.pick(GatewayName.RAZORPAY)).isSameAs(razorpayGateway);
  }

  @Test
  void pickReturnsCashfreeWhenRequested() {
    assertThat(routerService.pick(GatewayName.CASHFREE)).isSameAs(cashfreeGateway);
  }
}
