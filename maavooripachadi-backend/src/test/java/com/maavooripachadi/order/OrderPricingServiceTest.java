package com.maavooripachadi.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPricingServiceTest {

    private OrderPricingService service;

    @BeforeEach
    void setUp() {
        service = new OrderPricingService();
    }

    @Test
    void shippingPaiseForPincodeReturnsFlatRate() {
        assertThat(service.shippingPaiseForPincode("500032")).isEqualTo(5_000);
    }

    @Test
    void taxPaiseOnSubtotalCalculatesFivePercentRounded() {
        assertThat(service.taxPaiseOnSubtotal(10_000)).isEqualTo(500);
        assertThat(service.taxPaiseOnSubtotal(12_345)).isEqualTo(Math.round(12_345 * 0.05f));
    }

    @Test
    void discountAppliesUpToTenPercentCappedAtThousand() {
        assertThat(service.discountPaise("SAVE10", 5_000)).isEqualTo(500);
        assertThat(service.discountPaise("SAVE10", 20_000)).isEqualTo(1_000);
        assertThat(service.discountPaise(null, 20_000)).isZero();
    }
}
