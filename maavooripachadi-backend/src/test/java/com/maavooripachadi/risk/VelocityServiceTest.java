package com.maavooripachadi.risk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VelocityServiceTest {

    @Mock private VelocityWindowRepository windowRepository;
    @Mock private RiskEventRepository eventRepository;

    private VelocityService service;

    @BeforeEach
    void setUp() {
        service = new VelocityService(windowRepository, eventRepository);
    }

    @Test
    void checkReturnsReasonWhenThresholdExceeded() {
        VelocityWindow window = new VelocityWindow();
        window.setKeyExpression("ip:{ip}");
        window.setWindowSeconds(60);
        window.setMaxCount(3);

        when(windowRepository.findAll()).thenReturn(List.of(window));
        when(eventRepository.countSinceForAny(any(OffsetDateTime.class), eq("10.0.0.1"), any(), any())).thenReturn(5L);

        String result = service.check("10.0.0.1", null, null);

        assertThat(result).isEqualTo("velocity:ip:{ip}>3");
    }

    @Test
    void checkReturnsNullWhenUnderThreshold() {
        when(windowRepository.findAll()).thenReturn(List.of());

        String result = service.check("10.0.0.1", "user@example.com", "device-1");

        assertThat(result).isNull();
    }
}
