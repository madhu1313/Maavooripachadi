package com.maavooripachadi.pricing;

import com.maavooripachadi.pricing.dto.PriceQuoteRequest;
import com.maavooripachadi.pricing.dto.PriceQuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PricingControllerTest {

    private PricingService pricingService;
    private PricingController controller;

    @BeforeEach
    void setUp() {
        pricingService = mock(PricingService.class);
        controller = new PricingController(pricingService);
    }

    @Test
    void quoteDelegatesToPricingService() {
        PriceQuoteRequest request = new PriceQuoteRequest();
        PriceQuoteResponse response = new PriceQuoteResponse();
        response.setTotalPaise(4200);
        when(pricingService.quote(request)).thenReturn(response);

        PriceQuoteResponse result = controller.quote(request);

        assertThat(result).isSameAs(response);
        verify(pricingService).quote(request);
    }

    @Test
    void convertReturnsConvertedAmount() {
        when(pricingService.convertInrTo("USD", 10_000)).thenReturn(1234);

        Map<String, Object> payload = controller.convert(10_000, "USD");

        assertThat(payload).containsEntry("amount", 1234);
        assertThat(payload).containsEntry("currency", "USD");
        verify(pricingService).convertInrTo("USD", 10_000);
    }
}
