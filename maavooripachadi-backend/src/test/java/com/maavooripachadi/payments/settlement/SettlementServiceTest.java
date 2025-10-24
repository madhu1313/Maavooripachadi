package com.maavooripachadi.payments.settlement;

import com.maavooripachadi.payments.settlement.dto.BatchSummaryResponse;
import com.maavooripachadi.payments.settlement.dto.SettlementIngestRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock private SettlementBatchRepository batchRepository;
    @Mock private SettlementLineRepository lineRepository;
    @Mock private ReconcileFileRepository fileRepository;

    private SettlementService service;

    @BeforeEach
    void setUp() {
        service = new SettlementService(batchRepository, lineRepository, fileRepository);
    }

    @Test
    void ingestWithoutUrlCreatesBatch() {
        when(batchRepository.save(any(SettlementBatch.class))).thenAnswer(invocation -> {
            SettlementBatch batch = invocation.getArgument(0);
            ReflectionTestUtils.setField(batch, "id", 101L);
            return batch;
        });

        SettlementIngestRequest request = new SettlementIngestRequest();
        request.setGateway("RAZORPAY");
        request.setFileId("file-1");
        request.setChecksum("abc123");
        request.setPayoutDate("2025-10-23");

        SettlementBatch batch = service.ingest(request);

        assertThat(batch.getGateway()).isEqualTo("RAZORPAY");
        assertThat(batch.getFileId()).isEqualTo("file-1");
        assertThat(batch.getChecksum()).isEqualTo("abc123");
        assertThat(batch.getPayoutDate()).isEqualTo(LocalDate.parse("2025-10-23"));
        assertThat(batch.getCountTxns()).isZero();
        assertThat(batch.getTotalAmountPaise()).isZero();
        verify(lineRepository, never()).save(any());
    }

    @Test
    void listMapsBatchesToSummaryDtos() {
        SettlementBatch batch = new SettlementBatch();
        ReflectionTestUtils.setField(batch, "id", 55L);
        batch.setGateway("CASHFREE");
        batch.setPayoutDate(LocalDate.parse("2025-10-01"));
        batch.setCountTxns(3);
        batch.setTotalAmountPaise(45_000);
        when(batchRepository.findAll()).thenReturn(List.of(batch));

        List<BatchSummaryResponse> summaries = service.list();

        assertThat(summaries).hasSize(1);
        BatchSummaryResponse summary = summaries.get(0);
        assertThat(summary.getId()).isEqualTo(55L);
        assertThat(summary.getGateway()).isEqualTo("CASHFREE");
        assertThat(summary.getPayoutDate()).isEqualTo("2025-10-01");
        assertThat(summary.getCountTxns()).isEqualTo(3);
        assertThat(summary.getTotalAmountPaise()).isEqualTo(45_000);
    }
}
