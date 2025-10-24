package com.maavooripachadi.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OrderNumberServiceTest {

    private OrderRepository repository;
    private OrderNumberService service;
    private LocalDate today;
    private String todayPrefix;

    @BeforeEach
    void setUp() {
        repository = mock(OrderRepository.class);
        service = new OrderNumberService(repository);
        today = LocalDate.now();
        todayPrefix = String.format("ORD-%04d%02d%02d-", today.getYear(), today.getMonthValue(), today.getDayOfMonth());
    }

    @Test
    void nextUsesExistingHighestSequence() {
        Order last = new Order();
        last.setOrderNo(todayPrefix + "000105");
        when(repository.findFirstByOrderNoStartingWithOrderByOrderNoDesc(todayPrefix)).thenReturn(Optional.of(last));

        service.init();

        String first = service.next();
        String second = service.next();

        assertThat(first).isEqualTo(todayPrefix + "000106");
        assertThat(second).isEqualTo(todayPrefix + "000107");
        verify(repository, atLeastOnce()).findFirstByOrderNoStartingWithOrderByOrderNoDesc(todayPrefix);
    }

    @Test
    void nextResetsCounterWhenDateChanges() {
        when(repository.findFirstByOrderNoStartingWithOrderByOrderNoDesc(anyString())).thenReturn(Optional.empty());

        service.init();
        // simulate date switch by setting counterDate to yesterday and a non-zero counter
        ReflectionTestUtils.setField(service, "counterDate", today.minusDays(1));
        AtomicInteger counter = (AtomicInteger) ReflectionTestUtils.getField(service, "counter");
        counter.set(42);

        String next = service.next();

        assertThat(next).isEqualTo(todayPrefix + "000001");
    }
}
