package com.maavooripachadi.metrics;

import com.maavooripachadi.metrics.dto.EventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MetricsIngestControllerTest {

  private MetricsService metricsService;
  private MetricsIngestController controller;

  @BeforeEach
  void setUp() {
    metricsService = mock(MetricsService.class);
    controller = new MetricsIngestController(metricsService);
  }

  @Test
  void recordEndpointDelegatesToService() {
    EventRequest request = new EventRequest();
    request.setName("checkout_start");
    MetricEvent persisted = new MetricEvent();
    when(metricsService.record(request)).thenReturn(persisted);

    MetricEvent response = controller.record(request);

    assertThat(response).isSameAs(persisted);
    verify(metricsService).record(request);
  }
}
