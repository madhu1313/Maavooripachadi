package com.maavooripachadi.payments.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GatewayRouterTest {

  private PaymentRouterService routerService;
  private PaymentGateway razorpayGateway;
  private GatewayRouter router;

  @BeforeEach
  void setUp() {
    routerService = mock(PaymentRouterService.class);
    razorpayGateway = mock(PaymentGateway.class);
    router = new GatewayRouter(routerService);
  }

  @Test
  void chooseDelegatesToRouterService() {
    when(routerService.pick(GatewayName.RAZORPAY)).thenReturn(razorpayGateway);

    PaymentGateway chosen = router.choose("razorpay");

    assertThat(chosen).isSameAs(razorpayGateway);
  }

  @Test
  void chooseThrowsForUnknownGateway() {
    assertThatThrownBy(() -> router.choose("unknown"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown gateway");
  }
}
