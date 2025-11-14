package com.maavooripachadi.shipping.dto;

import com.maavooripachadi.shipping.ServiceLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateQuoteRequestTest {

    @Test
    void settersPopulateRequest() {
        RateQuoteRequest request = new RateQuoteRequest();
        request.setFromPincode("500001");
        request.setToPincode("560001");
        request.setWeightGrams(1200);
        request.setLengthCm(20);
        request.setWidthCm(10);
        request.setHeightCm(5);
        request.setServiceLevel(ServiceLevel.EXPRESS);

        assertEquals("500001", request.getFromPincode());
        assertEquals("560001", request.getToPincode());
        assertEquals(1200, request.getWeightGrams());
        assertEquals(20, request.getLengthCm());
        assertEquals(10, request.getWidthCm());
        assertEquals(5, request.getHeightCm());
        assertEquals(ServiceLevel.EXPRESS, request.getServiceLevel());
    }

    @Test
    void defaultServiceLevelIsStandard() {
        RateQuoteRequest request = new RateQuoteRequest();
        assertEquals(ServiceLevel.STANDARD, request.getServiceLevel());
    }
}
