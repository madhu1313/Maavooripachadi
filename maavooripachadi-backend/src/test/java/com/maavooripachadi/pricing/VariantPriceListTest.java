package com.maavooripachadi.pricing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariantPriceListTest {

    @Test
    void gettersAndSetters() {
        VariantPriceList list = new VariantPriceList();
        list.setVariantId(11L);
        list.setListCode("DEFAULT");
        list.setPriceMrp(39900);
        list.setPriceSale(29900);

        assertEquals(11L, list.getVariantId());
        assertEquals("DEFAULT", list.getListCode());
        assertEquals(39900, list.getPriceMrp());
        assertEquals(29900, list.getPriceSale());
    }
}
