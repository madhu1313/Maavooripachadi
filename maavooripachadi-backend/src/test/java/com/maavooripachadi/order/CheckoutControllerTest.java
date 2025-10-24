package com.maavooripachadi.order;

import com.maavooripachadi.order.dto.CheckoutRequest;
import com.maavooripachadi.order.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CheckoutControllerTest {

    private OrderService orderService;
    private CheckoutController controller;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        controller = new CheckoutController(orderService);
    }

    @Test
    void checkoutDelegatesToOrderService() {
        CheckoutRequest request = new CheckoutRequest();
        Order order = new Order();
        order.setOrderNo("ORD-1");
        order.setTotalPaise(25_000);
        when(orderService.checkout(request)).thenReturn(order);

        OrderResponse response = controller.checkout(request);

        assertThat(response.getOrderNo()).isEqualTo("ORD-1");
        assertThat(response.getTotalPaise()).isEqualTo(25_000);
        verify(orderService).checkout(request);
    }

    @Test
    void getReturnsOrderResponseFromService() {
        Order order = new Order();
        order.setOrderNo("ORD-2");
        order.setTotalPaise(10_000);
        when(orderService.getByOrderNo("ORD-2")).thenReturn(order);

        OrderResponse response = controller.get("ORD-2");

        assertThat(response.getOrderNo()).isEqualTo("ORD-2");
        verify(orderService).getByOrderNo("ORD-2");
    }
}
