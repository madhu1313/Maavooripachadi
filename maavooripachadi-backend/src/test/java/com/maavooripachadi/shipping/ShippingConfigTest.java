package com.maavooripachadi.shipping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ShippingConfigTest {

    @Test
    void configurationLoads() {
        ShippingConfig config = new ShippingConfig();
        assertNotNull(config);
    }
}
