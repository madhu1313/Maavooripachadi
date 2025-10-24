package com.maavooripachadi.pricing;

import com.maavooripachadi.pricing.dto.SetRateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FxServiceTest {

    private CurrencyRateRepository repository;
    private FxService service;

    @BeforeEach
    void setUp() {
        repository = mock(CurrencyRateRepository.class);
        service = new FxService(repository);
    }

    @Test
    void setRateCreatesOrUpdatesCurrencyRate() {
        SetRateRequest request = new SetRateRequest();
        request.setFrom("inr");
        request.setTo("usd");
        request.setRate(0.012d);

        CurrencyRate existing = new CurrencyRate();
        when(repository.findByFromAndTo("INR", "USD")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        CurrencyRate result = service.setRate(request);

        assertThat(result.getFrom()).isEqualTo("INR");
        assertThat(result.getTo()).isEqualTo("USD");
        assertThat(result.getRate()).isEqualTo(0.012d);
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void setRateCreatesNewEntryWhenMissing() {
        when(repository.findByFromAndTo(any(), any())).thenReturn(Optional.empty());
        when(repository.save(any(CurrencyRate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SetRateRequest request = new SetRateRequest();
        request.setFrom("INR");
        request.setTo("EUR");
        request.setRate(0.011d);

        CurrencyRate result = service.setRate(request);

        assertThat(result.getFrom()).isEqualTo("INR");
        assertThat(result.getTo()).isEqualTo("EUR");
        assertThat(result.getRate()).isEqualTo(0.011d);
        assertThat(result.getUpdatedAt()).isNotNull();
    }
}
