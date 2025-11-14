package com.maavooripachadi.payments.settlement.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettlementReconcileRequestTest {

    @Test
    void getterSetterRoundTrip() {
        SettlementReconcileRequest request = new SettlementReconcileRequest();
        request.setFileId("file-123");
        request.setUrl("https://cdn.example/file.csv");

        assertEquals("file-123", request.getFileId());
        assertEquals("https://cdn.example/file.csv", request.getUrl());
    }
}
