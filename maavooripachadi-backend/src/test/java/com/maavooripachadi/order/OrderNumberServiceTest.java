package com.maavooripachadi.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderNumberServiceTest {

    @Mock
    private OrderRepository orders;

    private OrderNumberService service;
    private LocalDate today;
    private String prefix;

    @BeforeEach
    void setUp() {
        service = new OrderNumberService(orders);
        today = LocalDate.now();
        prefix = String.format("ORD-%04d%02d%02d-", today.getYear(), today.getMonthValue(), today.getDayOfMonth());
    }

    @Test
    void generatesSequentialOrderNumbers() {
        when(orders.findFirstByOrderNoStartingWithOrderByOrderNoDesc(prefix)).thenReturn(Optional.empty());

        String first = service.next();
        String second = service.next();

        assertEquals(prefix + "000001", first);
        assertEquals(prefix + "000002", second);
        verify(orders, times(1)).findFirstByOrderNoStartingWithOrderByOrderNoDesc(prefix);
    }

    @Test
    void continuesFromExistingSeed() {
        Order latest = new Order();
        latest.setOrderNo(prefix + "000099");
        when(orders.findFirstByOrderNoStartingWithOrderByOrderNoDesc(prefix)).thenReturn(Optional.of(latest));

        String next = service.next();

        assertEquals(prefix + "000100", next);
    }

    @Test
    void resetsCounterWhenDateChanges() {
        when(orders.findFirstByOrderNoStartingWithOrderByOrderNoDesc(prefix))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(orderWith(prefix + "000010")));

        service.next(); // initialise for today
        // simulate day change
        ReflectionTestUtils.setField(service, "counterDate", today.minusDays(1));

        String next = service.next();

        assertEquals(prefix + "000011", next);
        verify(orders, times(2)).findFirstByOrderNoStartingWithOrderByOrderNoDesc(eq(prefix));
    }

    private static Order orderWith(String orderNo) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        return order;
    }
}
