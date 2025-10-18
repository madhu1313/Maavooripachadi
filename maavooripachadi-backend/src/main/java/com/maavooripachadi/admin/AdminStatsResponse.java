package com.maavooripachadi.admin;


/**
 * Lightweight DTO returned by /api/v1/admin/stats
 * Values are Micrometer counter snapshots; doubles are fine for counters.
 */
public record AdminStatsResponse(
        double checkoutStarts,
        double paymentSuccess,
        double webhooksOk
) {}