package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.RateQuoteRequest;
import com.maavooripachadi.shipping.dto.RateQuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateServiceTest {

    private RateCardRepository repository;
    private RateService service;

    @BeforeEach
    void setUp() {
        repository = mock(RateCardRepository.class);
        service = new RateService(repository);
    }

    @Test
    void quoteReturnsEmptyListWhenNoRateMatchesPincode() {
        RateQuoteRequest request = new RateQuoteRequest();
        request.setToPincode("500032");

        ShippingZone zone = new ShippingZone();
        zone.setPincodesCsv("600001,600002");
        RateCard card = new RateCard();
        card.setZone(zone);
        when(repository.findAll()).thenReturn(List.of(card));

        List<RateQuoteResponse> responses = service.quote(request);

        assertThat(responses).isEmpty();
        verify(repository).findAll();
    }

    @Test
    void quoteCalculatesSlabbedPriceWhenWeightAboveBase() {
        RateQuoteRequest request = new RateQuoteRequest();
        request.setToPincode("500032");
        request.setWeightGrams(1300);

        ShippingZone zone = new ShippingZone();
        zone.setPincodesCsv("400001,500032,600001");

        RateCard card = new RateCard();
        card.setCarrier(CarrierCode.SHIPROCKET);
        card.setService(ServiceLevel.STANDARD);
        card.setZone(zone);
        card.setBasePaise(1000);
        card.setPer500gPaise(250);

        when(repository.findAll()).thenReturn(List.of(card));

        List<RateQuoteResponse> responses = service.quote(request);

        assertThat(responses)
            .singleElement()
            .satisfies(response -> {
                assertThat(response.getCarrier()).isEqualTo(CarrierCode.SHIPROCKET);
                assertThat(response.getServiceLevel()).isEqualTo(ServiceLevel.STANDARD);
                // weight 1300 => extra 800 -> ceil(800 / 500)=2 slabs -> 1000 + 2*250 = 1500
                assertThat(response.getAmountPaise()).isEqualTo(1500);
            });
    }

    @Test
    void quoteHandlesZeroOrNegativeWeightByTreatingAsOneGram() {
        RateQuoteRequest request = new RateQuoteRequest();
        request.setToPincode("500032");
        request.setWeightGrams(0);

        ShippingZone zone = new ShippingZone();
        zone.setPincodesCsv("500032");

        RateCard card = new RateCard();
        card.setCarrier(CarrierCode.SHIPROCKET);
        card.setService(ServiceLevel.EXPRESS);
        card.setZone(zone);
        card.setBasePaise(999);
        card.setPer500gPaise(500);

        when(repository.findAll()).thenReturn(List.of(card));

        List<RateQuoteResponse> responses = service.quote(request);

        assertThat(responses)
            .singleElement()
            .extracting(RateQuoteResponse::getAmountPaise)
            .isEqualTo(999);

        // Ensure the zone CSV was inspected and repository called only once.
        verify(repository, times(1)).findAll();
    }
}
