package com.maavooripachadi.disputes.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public record CreateDisputeRequest(
        @NotBlank String gateway,
        @NotBlank String providerCaseId,
        String orderNo,
        Long paymentAttemptId,
        String reason,
        String type,
        @Min(1) int amountPaise,
        String currency
) {}