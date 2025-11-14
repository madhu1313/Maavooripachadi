package com.maavooripachadi.shipping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShippingPropertiesTest {

    @Test
    void exposesReasonableDefaults() {
        ShippingProperties properties = new ShippingProperties();

        assertEquals("500001", properties.getDefaultFromPincode());
        assertEquals(CarrierCode.SHIPROCKET, properties.getDefaultCarrier());
    }

    @Test
    void settersOverrideDefaults() {
        ShippingProperties properties = new ShippingProperties();
        properties.setDefaultFromPincode("560001");
        properties.setDefaultCarrier(CarrierCode.DTDC);

        assertEquals("560001", properties.getDefaultFromPincode());
        assertEquals(CarrierCode.DTDC, properties.getDefaultCarrier());
    }
}
