package com.maavooripachadi.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CounterRollupServiceTest {

  private MetricEventRepository events;
  private MetricCounterRepository counters;
  private CounterRollupService service;

  @BeforeEach
  void setUp() {
    events = mock(MetricEventRepository.class);
    counters = mock(MetricCounterRepository.class);
    service = new CounterRollupService(events, counters);
  }

  @Test
  void rollupMinuteAggregatesEventValuesIntoCounter() {
    MetricEvent e1 = new MetricEvent();
    e1.setName("checkout_start");
    e1.setValue(400);
    e1.setOccurredAt(OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(30));

    MetricEvent e2 = new MetricEvent();
    e2.setName("checkout_start");
    e2.setValue(600);
    e2.setOccurredAt(OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(40));

    when(events.findByNameAndWindow(eq("checkout_start"), any(), any(), any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(e1, e2)));
    when(events.findByNameAndWindow(eq("payment_success"), any(), any(), any(org.springframework.data.domain.Pageable.class)))
        .thenReturn(Page.empty());
    when(counters.save(any(MetricCounter.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.rollupMinute();

    var counterCaptor = org.mockito.ArgumentCaptor.forClass(MetricCounter.class);
    verify(counters, times(1)).save(counterCaptor.capture());
    MetricCounter counter = counterCaptor.getValue();
    assertThat(counter.getName()).isEqualTo("checkout_start");
    assertThat(counter.getGranularity()).isEqualTo(MetricGranularity.MINUTE);
    assertThat(counter.getCount()).isEqualTo(2);
    assertThat(counter.getSum()).isEqualTo(1000.0);
    assertThat(counter.getMinVal()).isEqualTo(400.0);
    assertThat(counter.getMaxVal()).isEqualTo(600.0);
    assertThat(counter.getWindowStart()).isNotNull();
    assertThat(counter.getWindowEnd()).isNotNull();
  }
}
