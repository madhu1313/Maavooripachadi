package com.maavooripachadi.disputes.dto;


import com.maavooripachadi.disputes.*;
import java.time.OffsetDateTime;


public record DisputeResponse(
        Long id,
        String gateway,
        String providerCaseId,
        Long paymentAttemptId,
        String orderNo,
        DisputeStatus status,
        String reason,
        String type,
        int amountPaise,
        String currency,
        OffsetDateTime evidenceDueAt,
        OffsetDateTime decidedAt,
        String notes
) {
    public static DisputeResponse from(Dispute d){
        return new DisputeResponse(
                d.getId(), d.getGateway(), d.getProviderCaseId(), d.getPaymentAttemptId(), d.getOrderNo(), d.getStatus(),
                d.getReason(), d.getType(), d.getAmountPaise(), d.getCurrency(), d.getEvidenceDueAt(), d.getDecidedAt(), d.getNotes()
        );
    }
}