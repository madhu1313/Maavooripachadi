package com.maavooripachadi.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PriceListServiceTest {

    private CurrencyRateRepository rateRepository;
    private PriceListService service;

    @BeforeEach
    void setUp() {
        rateRepository = mock(CurrencyRateRepository.class);
        service = new PriceListService(rateRepository);
    }

    @Test
    void convertReturnsSameAmountForInr() {
        assertThat(service.convert(5_000, "INR")).isEqualTo(5_000);
        assertThat(service.convert(5_000, null)).isEqualTo(5_000);
    }

    @Test
    void convertUsesRateWhenAvailable() {
        CurrencyRate rate = new CurrencyRate();
        rate.setRate(0.014); // INR -> USD
        when(rateRepository.findByFromAndTo("INR", "USD")).thenReturn(Optional.of(rate));

        int result = service.convert(10_000, "USD");

        assertThat(result).isEqualTo((int) Math.round((10_000 / 100.0d) * 0.014 * 100.0d));
    }

    @Test
    void convertFallsBackWhenRateMissing() {
        when(rateRepository.findByFromAndTo("INR", "EUR")).thenReturn(Optional.empty());

        int result = service.convert(8_500, "EUR");

        assertThat(result).isEqualTo(8_500);
    }
}
