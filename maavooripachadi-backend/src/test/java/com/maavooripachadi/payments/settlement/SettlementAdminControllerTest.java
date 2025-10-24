package com.maavooripachadi.payments.settlement;

import com.maavooripachadi.payments.settlement.dto.BatchSummaryResponse;
import com.maavooripachadi.payments.settlement.dto.SettlementIngestRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementAdminControllerTest {

    @Mock
    private SettlementService service;

    private SettlementAdminController controller;

    @BeforeEach
    void setUp() {
        controller = new SettlementAdminController(service);
    }

    @Test
    void ingestDelegatesToService() {
        SettlementIngestRequest request = new SettlementIngestRequest();
        SettlementBatch batch = new SettlementBatch();
        batch.setGateway("RAZORPAY");
        batch.setPayoutDate(LocalDate.now());
        when(service.ingest(request)).thenReturn(batch);

        SettlementBatch result = controller.ingest(request);

        assertThat(result).isSameAs(batch);
        verify(service).ingest(request);
    }

    @Test
    void listReturnsSummaries() {
        BatchSummaryResponse summary = new BatchSummaryResponse();
        summary.setGateway("CASHFREE");
        when(service.list()).thenReturn(List.of(summary));

        List<BatchSummaryResponse> response = controller.list();

        assertThat(response).containsExactly(summary);
        verify(service).list();
    }
}
