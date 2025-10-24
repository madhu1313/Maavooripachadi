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
class EinvoiceServiceTest {

    @Mock private EinvoiceMetaRepository repository;
    private EinvoiceService service;

    @BeforeEach
    void setUp() {
        service = new EinvoiceService(repository);
    }

    @Test
    void mapBuildsStubbedDto() {
        EinvoiceDTO dto = service.map("ORD-1");

        assertThat(dto.orderNo()).isEqualTo("ORD-1");
        assertThat(dto.items()).hasSize(1);
        assertThat(dto.totalValuePaise()).isPositive();
    }

    @Test
    void registerCreatesNewMetaWhenMissing() {
        when(repository.findByOrderNo("ORD-2")).thenReturn(Optional.empty());
        when(repository.save(any(EinvoiceMeta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EinvoiceDTO dto = service.map("ORD-2");
        EinvoiceMeta meta = service.register(dto);

        assertThat(meta.getOrderNo()).isEqualTo("ORD-2");
        assertThat(meta.getIrn()).startsWith("IRN-");
        assertThat(meta.getAckNo()).startsWith("ACK-");
        assertThat(meta.getAckDt()).isNotNull();
    }

    @Test
    void registerUpdatesExistingMeta() {
        EinvoiceMeta existing = new EinvoiceMeta();
        existing.setOrderNo("ORD-3");
        existing.setIrn("IRN-OLD");
        when(repository.findByOrderNo("ORD-3")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        EinvoiceDTO dto = service.map("ORD-3");
        EinvoiceMeta saved = service.register(dto);

        assertThat(saved.getIrn()).startsWith("IRN-");
        verify(repository).save(existing);
    }
}
