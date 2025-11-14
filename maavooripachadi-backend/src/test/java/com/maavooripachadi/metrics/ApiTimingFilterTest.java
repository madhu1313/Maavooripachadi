package com.maavooripachadi.metrics;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiTimingFilterTest {

    private ApiMetricsService metrics;
    private ApiTimingFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        metrics = mock(ApiMetricsService.class);
        filter = new ApiTimingFilter(metrics);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    void savesMetricForApiRequests() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/orders");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        filter.doFilterInternal(request, response, chain);

        ArgumentCaptor<ApiMetric> captor = ArgumentCaptor.forClass(ApiMetric.class);
        verify(metrics).save(captor.capture());
        ApiMetric metric = captor.getValue();
        assertEquals("/api/orders", metric.getPath());
        assertEquals("GET", metric.getMethod());
        assertEquals(200, metric.getStatus());
        assertEquals("127.0.0.1", metric.getIp());
    }

    @Test
    void ignoresNonApiRequests() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/health");

        filter.doFilterInternal(request, response, chain);

        verify(metrics, never()).save(any());
    }
}
