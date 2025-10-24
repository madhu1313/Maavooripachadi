package com.maavooripachadi.compliance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GstServiceTest {

    @Mock private Gstr1SummaryRepository repository;
    private GstService service;

    @BeforeEach
    void setUp() {
        service = new GstService(repository);
    }

    @Test
    void gstr1ReturnsExistingSummary() {
        Gstr1Summary summary = new Gstr1Summary();
        summary.setPeriod("2025-08");
        summary.setB2cCount(5);
        summary.setB2cTaxablePaise(12_000);
        summary.setB2cTaxPaise(600);
        summary.setB2bCount(1);
        summary.setB2bTaxablePaise(50_000);
        summary.setB2bTaxPaise(9_000);

        when(repository.findByPeriod("2025-08")).thenReturn(Optional.of(summary));

        Map<String, Object> payload = service.gstr1("2025-08");

        assertThat(payload)
            .containsEntry("period", "2025-08")
            .containsEntry("b2cCount", 5)
            .containsEntry("b2cTaxablePaise", 12_000)
            .containsEntry("b2bTaxPaise", 9_000);
    }

    @Test
    void gstr1SeedsEmptySummaryWhenMissing() {
        when(repository.findByPeriod("2025-09")).thenReturn(Optional.empty());
        when(repository.save(any(Gstr1Summary.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> payload = service.gstr1("2025-09");

        assertThat(payload)
            .containsEntry("period", "2025-09")
            .containsEntry("b2cCount", 0)
            .containsEntry("b2bCount", 0);
    }
}
