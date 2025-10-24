package com.maavooripachadi.compliance;

import com.maavooripachadi.compliance.ComplianceAdminController.AccountingQueueResponse;
import com.maavooripachadi.compliance.ComplianceAdminController.EinvoiceResponse;
import com.maavooripachadi.compliance.ComplianceAdminController.EwaybillResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplianceAdminControllerTest {

    @Mock private EinvoiceService einvoiceService;
    @Mock private EwaybillService ewaybillService;
    @Mock private AccountingSyncService accountingSyncService;
    @Mock private GstService gstService;

    private ComplianceAdminController controller;

    @BeforeEach
    void setUp() {
        controller = new ComplianceAdminController(einvoiceService, ewaybillService, accountingSyncService, gstService);
    }

    @Test
    void generateEinvoiceReturnsAckDetails() {
        EinvoiceDTO dto = new EinvoiceDTO("ORD-11", "", "", "", "", "", 100, 100, 0, 0, 0, java.util.List.of());
        EinvoiceMeta meta = new EinvoiceMeta();
        meta.setOrderNo("ORD-11");
        meta.setIrn("IRN-ORD-11");
        meta.setAckNo("ACK-123");
        meta.setAckDt(OffsetDateTime.now());
        meta.setSignedQr("QR://ORD-11");

        when(einvoiceService.map("ORD-11")).thenReturn(dto);
        when(einvoiceService.register(dto)).thenReturn(meta);

        EinvoiceResponse response = controller.generateEinvoice("ORD-11");

        assertThat(response.irn()).isEqualTo("IRN-ORD-11");
        assertThat(response.ackNo()).isEqualTo("ACK-123");
        verify(einvoiceService).register(dto);
    }

    @Test
    void createEwaybillReturnsDetails() {
        Ewaybill bill = new Ewaybill();
        bill.setOrderNo("ORD-12");
        bill.setEwbNo("EWB-12");
        bill.setValidUpto(OffsetDateTime.now().plusDays(1));

        when(ewaybillService.create("ORD-12", "TS09AB1234", 150)).thenReturn(bill);

        EwaybillResponse response = controller.createEwaybill("ORD-12", "TS09AB1234", 150);

        assertThat(response.ewbNo()).isEqualTo("EWB-12");
        assertThat(response.validUpto()).isNotBlank();
    }

    @Test
    void queueAccountingReturnsQueuedId() {
        AccountingSync sync = new AccountingSync();
        ReflectionTestUtils.setField(sync, "id", 42L);
        when(accountingSyncService.queue("ORDER", "ORD-13", "ZOHO")).thenReturn(sync);

        AccountingQueueResponse response = controller.queueAccounting("ORDER", "ORD-13");

        assertThat(response.queued()).isTrue();
        assertThat(response.id()).isEqualTo(42L);
    }

    @Test
    void gstr1DelegatesToGstService() {
        when(gstService.gstr1("2025-09")).thenReturn(Map.of("period", "2025-09"));

        Map<String, Object> payload = controller.gstr1("2025-09");

        assertThat(payload).containsEntry("period", "2025-09");
        verify(gstService).gstr1("2025-09");
    }
}
