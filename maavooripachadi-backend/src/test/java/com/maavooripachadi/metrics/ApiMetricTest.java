package com.maavooripachadi.metrics;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiMetricTest {

    @Test
    void settersPopulateMetric() {
        ApiMetric metric = new ApiMetric();
        OffsetDateTime occurred = OffsetDateTime.now().minusMinutes(5);
        metric.setMethod("POST");
        metric.setPath("/api/v1/orders");
        metric.setStatus(201);
        metric.setDurationMs(250);
        metric.setUserId("user-1");
        metric.setIp("10.0.0.1");
        metric.setOccurredAt(occurred);

        assertEquals("POST", metric.getMethod());
        assertEquals("/api/v1/orders", metric.getPath());
        assertEquals(201, metric.getStatus());
        assertEquals(250, metric.getDurationMs());
        assertEquals("user-1", metric.getUserId());
        assertEquals("10.0.0.1", metric.getIp());
        assertEquals(occurred, metric.getOccurredAt());
    }

    @Test
    void defaultOccurredAtIsSet() {
        ApiMetric metric = new ApiMetric();
        assertNotNull(metric.getOccurredAt());
    }
}
