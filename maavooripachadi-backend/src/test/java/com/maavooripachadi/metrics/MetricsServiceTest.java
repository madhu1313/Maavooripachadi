package com.maavooripachadi.metrics;

import com.maavooripachadi.metrics.dto.EventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MetricsServiceTest {

  private MetricEventRepository events;
  private MetricCounterRepository counters;
  private MetricsService service;

  @BeforeEach
  void setUp() {
    events = mock(MetricEventRepository.class);
    counters = mock(MetricCounterRepository.class);
    service = new MetricsService(events, counters);
  }

  @Test
  void recordPersistsEventWithProvidedAttributes() {
    EventRequest request = new EventRequest();
    request.setName("checkout_start");
    request.setUnit("count");
    request.setValue(2.0);
    request.setTagsJson("{\"channel\":\"web\"}");
    request.setOccurredAt("2025-01-01T10:15:30Z");

    when(events.save(any(MetricEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MetricEvent saved = service.record(request);

    assertThat(saved.getName()).isEqualTo("checkout_start");
    assertThat(saved.getUnit()).isEqualTo("count");
    assertThat(saved.getValue()).isEqualTo(2.0);
    assertThat(saved.getTagsJson()).isEqualTo("{\"channel\":\"web\"}");
    assertThat(saved.getOccurredAt()).isEqualTo(OffsetDateTime.parse("2025-01-01T10:15:30Z"));

    verify(events).save(saved);
  }

  @Test
  void recordDefaultsValueAndTimestampWhenMissingOrInvalid() {
    EventRequest request = new EventRequest();
    request.setName("payment_success");
    request.setUnit("ms");
    request.setValue(null);
    request.setOccurredAt("not-a-timestamp");

    when(events.save(any(MetricEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

    OffsetDateTime before = OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(1);
    MetricEvent saved = service.record(request);
    OffsetDateTime after = OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(1);

    assertThat(saved.getValue()).isEqualTo(1.0);
    assertThat(saved.getOccurredAt()).isBetween(before, after);
  }

  @Test
  void windowDelegatesToRepositoryWithPaging() {
    Page<MetricEvent> page = new PageImpl<>(List.of(new MetricEvent()));
    when(events.findByNameAndWindow("checkout_start",
        OffsetDateTime.parse("2025-01-01T00:00:00Z"),
        OffsetDateTime.parse("2025-01-02T00:00:00Z"),
        PageRequest.of(1, 50, org.springframework.data.domain.Sort.by("occurredAt"))))
        .thenReturn(page);

    Page<MetricEvent> result = service.window(
        "checkout_start",
        OffsetDateTime.parse("2025-01-01T00:00:00Z"),
        OffsetDateTime.parse("2025-01-02T00:00:00Z"),
        1,
        50);

    assertThat(result).isSameAs(page);
  }
}
