package com.maavooripachadi.admin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdminRateLimitGuardTest {

    private final AdminRateLimitGuard guard = new AdminRateLimitGuard();

    @Test
    void allowsRequestsWithinThreshold() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                guard.check("/admin/stats", "admin@maavooripachadi.com");
            }
        });
    }

    @Test
    void throwsWhenThresholdExceeded() {
        assertThrows(AdminRateLimitGuard.RateLimitException.class, () -> {
            for (int i = 0; i < 11; i++) {
                guard.check("/admin/stats", "admin@maavooripachadi.com");
            }
        });
    }
}
