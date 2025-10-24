package com.maavooripachadi.compliance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EwaybillServiceTest {

    @Mock private EwaybillRepository repository;
    private EwaybillService service;

    @BeforeEach
    void setUp() {
        service = new EwaybillService(repository);
    }

    @Test
    void createGeneratesNewRecordWhenMissing() {
        when(repository.findByOrderNo("ORD-4")).thenReturn(Optional.empty());
        when(repository.save(any(Ewaybill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ewaybill result = service.create("ORD-4", "TS09AB1234", 120);

        assertThat(result.getOrderNo()).isEqualTo("ORD-4");
        assertThat(result.getVehicleNo()).isEqualTo("TS09AB1234");
        assertThat(result.getDistanceKm()).isEqualTo(120);
        assertThat(result.getEwbNo()).startsWith("EWB-");
        assertThat(result.getValidUpto()).isAfter(OffsetDateTime.now());
    }

    @Test
    void createUpdatesExistingRecord() {
        Ewaybill existing = new Ewaybill();
        existing.setOrderNo("ORD-5");
        existing.setEwbNo("EWB-OLD");
        when(repository.findByOrderNo("ORD-5")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Ewaybill updated = service.create("ORD-5", "TS10XY9999", 80);

        assertThat(updated.getEwbNo()).isEqualTo("EWB-OLD");
        assertThat(updated.getVehicleNo()).isEqualTo("TS10XY9999");
        assertThat(updated.getDistanceKm()).isEqualTo(80);
        assertThat(updated.getValidUpto()).isAfter(OffsetDateTime.now());
    }
}
