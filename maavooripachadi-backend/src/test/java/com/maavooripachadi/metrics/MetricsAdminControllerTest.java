package com.maavooripachadi.metrics;

import com.maavooripachadi.metrics.dto.ApiQuery;
import com.maavooripachadi.metrics.dto.CounterQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MetricsAdminControllerTest {

  private MetricsService metricsService;
  private MetricCounterRepository counters;
  private ApiMetricsService apiMetricsService;
  private MetricsAdminController controller;

  @BeforeEach
  void setUp() {
    metricsService = mock(MetricsService.class);
    counters = mock(MetricCounterRepository.class);
    apiMetricsService = mock(ApiMetricsService.class);
    controller = new MetricsAdminController(metricsService, counters, apiMetricsService);
  }

  @Test
  void eventsEndpointDelegatesToMetricsService() {
    Page<MetricEvent> page = new PageImpl<>(List.of(new MetricEvent()));
    when(metricsService.window(
        eq("checkout_start"),
        eq(OffsetDateTime.parse("2025-01-01T00:00:00Z")),
        eq(OffsetDateTime.parse("2025-01-02T00:00:00Z")),
        eq(1),
        eq(25)))
        .thenReturn(page);

    Page<MetricEvent> response = controller.events(
        "checkout_start",
        "2025-01-01T00:00:00Z",
        "2025-01-02T00:00:00Z",
        1,
        25);

    assertThat(response).isSameAs(page);
  }

  @Test
  void countersEndpointQueriesRepository() {
    Page<MetricCounter> page = new PageImpl<>(List.of(new MetricCounter()));
    when(counters.findSeries(
        eq("checkout_start"),
        eq(MetricGranularity.MINUTE),
        eq(OffsetDateTime.parse("2025-01-01T00:00:00Z")),
        eq(OffsetDateTime.parse("2025-01-01T01:00:00Z")),
        eq(PageRequest.of(0, 200))))
        .thenReturn(page);

    CounterQuery query = new CounterQuery();
    query.setName("checkout_start");
    query.setGranularity(MetricGranularity.MINUTE);
    query.setFrom("2025-01-01T00:00:00Z");
    query.setTo("2025-01-01T01:00:00Z");

    Page<MetricCounter> response = controller.counters(query);

    assertThat(response).isSameAs(page);
  }

  @Test
  void apiEndpointDelegatesToApiMetricsService() {
    Page<ApiMetric> page = new PageImpl<>(List.of(new ApiMetric()));
    when(apiMetricsService.window(
        eq(OffsetDateTime.parse("2025-01-01T00:00:00Z")),
        eq(OffsetDateTime.parse("2025-01-01T01:00:00Z")),
        eq("/api/orders"),
        eq(0),
        eq(50)))
        .thenReturn(page);

    ApiQuery query = new ApiQuery();
    query.setFrom("2025-01-01T00:00:00Z");
    query.setTo("2025-01-01T01:00:00Z");
    query.setPath("/api/orders");
    query.setSize(50);

    Page<ApiMetric> response = controller.apis(query);

    assertThat(response).isSameAs(page);
  }
}
