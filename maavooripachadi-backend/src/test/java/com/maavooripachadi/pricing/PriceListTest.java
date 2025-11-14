package com.maavooripachadi.pricing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceListTest {

    @Test
    void gettersAndSetters() {
        PriceList priceList = new PriceList();
        priceList.setName("DEFAULT_USD");
        priceList.setCurrency("USD");
        priceList.setActive(false);
        priceList.setIsDefault(true);

        assertEquals("DEFAULT_USD", priceList.getName());
        assertEquals("USD", priceList.getCurrency());
        assertFalse(priceList.getActive());
        assertTrue(priceList.getIsDefault());
    }
}
