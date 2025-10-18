package com.maavooripachadi.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OrderPricingServiceTest {

  @Test
  void classLoads() {
    assertDoesNotThrow(() -> Class.forName("com.maavooripachadi.order.OrderPricingService"));
  }
}
