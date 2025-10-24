package com.maavooripachadi.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiMetricsServiceTest {

  private ApiMetricRepository repository;
  private ApiMetricsService service;

  @BeforeEach
  void setUp() {
    repository = mock(ApiMetricRepository.class);
    service = new ApiMetricsService(repository);
  }

  @Test
  void saveDelegatesToRepository() {
    ApiMetric metric = new ApiMetric();
    when(repository.save(metric)).thenReturn(metric);

    ApiMetric saved = service.save(metric);

    assertThat(saved).isSameAs(metric);
    verify(repository).save(metric);
  }

  @Test
  void windowUsesRepositoryWithSortByOccurredAtDescending() {
    Page<ApiMetric> page = new PageImpl<>(List.of(new ApiMetric()));
    when(repository.findWindow(
        OffsetDateTime.parse("2025-01-01T00:00:00Z"),
        OffsetDateTime.parse("2025-01-02T00:00:00Z"),
        "/orders",
        PageRequest.of(0, 25, Sort.by(Sort.Direction.DESC, "occurredAt"))))
        .thenReturn(page);

    Page<ApiMetric> result = service.window(
        OffsetDateTime.parse("2025-01-01T00:00:00Z"),
        OffsetDateTime.parse("2025-01-02T00:00:00Z"),
        "/orders",
        0,
        25);

    assertThat(result).isSameAs(page);
  }
}
