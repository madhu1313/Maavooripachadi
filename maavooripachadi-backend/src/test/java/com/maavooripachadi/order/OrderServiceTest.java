package com.maavooripachadi.order;

import com.maavooripachadi.order.dto.CheckoutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private OrderNumberService orderNumberService;
    private OrderPricingService orderPricingService;
    private OrderNotificationService orderNotificationService;

    private OrderService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        orderNumberService = mock(OrderNumberService.class);
        orderPricingService = mock(OrderPricingService.class);
        orderNotificationService = mock(OrderNotificationService.class);

        service = new OrderService(orderRepository, orderItemRepository, orderNumberService, orderPricingService, orderNotificationService);

        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void checkoutBuildsOrderAndPersistsWithPricing() {
        CheckoutRequest request = new CheckoutRequest();
        request.setCustomerEmail("  alice@example.com ");
        request.setCustomerPhone("99999");
        request.setCustomerName("Alice");
        request.setShipName("  ");
        request.setShipPhone(null);
        request.setShipLine1("Line 1");
        request.setShipLine2("  Apt 2 ");
        request.setShipCity("Hyderabad");
        request.setShipState("TS");
        request.setShipPincode("500032");
        request.setShipCountry("India");
        request.setCouponCode("SAVE10");
        request.setNotes("Please call");
        request.setPaymentGateway("razorpay");

        when(orderNumberService.next()).thenReturn("ORD-20251023-000123");
        when(orderPricingService.shippingPaiseForPincode("500032")).thenReturn(1_000);
        when(orderPricingService.discountPaise("SAVE10", 19_900)).thenReturn(2_000);
        when(orderPricingService.taxPaiseOnSubtotal(17_900)).thenReturn(895);

        Order order = service.checkout(request);

        assertThat(order.getOrderNo()).isEqualTo("ORD-20251023-000123");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getPaymentGateway()).isEqualTo("RAZORPAY");
        assertThat(order.getShipTo().getName()).isEqualTo("Alice");
        assertThat(order.getShipTo().getLine2()).isEqualTo("Apt 2");
        assertThat(order.getSubtotalPaise()).isEqualTo(19_900);
        assertThat(order.getShippingPaise()).isEqualTo(1_000);
        assertThat(order.getDiscountPaise()).isEqualTo(2_000);
        assertThat(order.getTaxPaise()).isEqualTo(895);
        assertThat(order.getTotalPaise()).isEqualTo(19_900 + 1_000 + 895 - 2_000);
        assertThat(order.getItems()).hasSize(1);

        verify(orderRepository).saveAndFlush(order);
        verify(orderNotificationService).notifyOrderPlaced(order);
    }

    @Test
    void getByOrderNoReturnsOrderWhenPresent() {
        Order stored = new Order();
        stored.setOrderNo("ORD-1");
        when(orderRepository.findByOrderNo("ORD-1")).thenReturn(Optional.of(stored));

        Order result = service.getByOrderNo("ORD-1");

        assertThat(result).isSameAs(stored);
    }

    @Test
    void getByOrderNoThrowsWhenMissing() {
        when(orderRepository.findByOrderNo("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByOrderNo("missing"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Order not found");
    }

    @Test
    void markPaidUpdatesStatusAndPersists() {
        Order stored = new Order();
        stored.setOrderNo("ORD-1");
        stored.setStatus(OrderStatus.PENDING);
        stored.setPaymentStatus(PaymentStatus.PENDING);

        when(orderRepository.findByOrderNo("ORD-1")).thenReturn(Optional.of(stored));

        Order updated = service.markPaid("ORD-1", "razorpay", "pay_123");

        assertThat(updated.getPaymentGateway()).isEqualTo("razorpay");
        assertThat(updated.getPaymentRef()).isEqualTo("pay_123");
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(updated.getPaymentStatus()).isEqualTo(PaymentStatus.CAPTURED);
        assertThat(updated.getPaidAt()).isNotNull();

        verify(orderRepository).save(stored);
    }

    @Test
    void cancelTransitionsPendingOrderToCancelled() {
        Order stored = new Order();
        stored.setOrderNo("ORD-2");
        stored.setStatus(OrderStatus.PENDING);

        when(orderRepository.findByOrderNo("ORD-2")).thenReturn(Optional.of(stored));

        Order cancelled = service.cancel("ORD-2", "Changed mind");

        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(stored);
    }

    @Test
    void cancelReturnsExistingWhenAlreadyCancelled() {
        Order stored = new Order();
        stored.setOrderNo("ORD-3");
        stored.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findByOrderNo("ORD-3")).thenReturn(Optional.of(stored));

        Order result = service.cancel("ORD-3", "ignore");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelRejectsPaidOrder() {
        Order stored = new Order();
        stored.setOrderNo("ORD-4");
        stored.setStatus(OrderStatus.PAID);

        when(orderRepository.findByOrderNo("ORD-4")).thenReturn(Optional.of(stored));

        assertThatThrownBy(() -> service.cancel("ORD-4", "cannot"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("refund");
    }
}
