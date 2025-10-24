package com.maavooripachadi.order;

import com.maavooripachadi.order.dto.CancelRequest;
import com.maavooripachadi.order.dto.MarkPaidRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderAdminControllerTest {

    private OrderRepository orderRepository;
    private OrderService orderService;
    private OrderNoteService noteService;
    private OrderAdminController controller;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderService = mock(OrderService.class);
        noteService = mock(OrderNoteService.class);
        controller = new OrderAdminController(orderRepository, orderService, noteService);
    }

    @Test
    void listWithoutStatusDelegatesToFindAll() {
        Page<Order> page = new PageImpl<>(List.of(new Order()));
        when(orderRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Order> result = controller.list(null, 0, 20);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findAll(any(PageRequest.class));
        verify(orderRepository, never()).findByStatus(any(), any());
    }

    @Test
    void listWithStatusFiltersByStatus() {
        Page<Order> page = new PageImpl<>(List.of(new Order()));
        when(orderRepository.findByStatus(eq(OrderStatus.PAID), any(PageRequest.class))).thenReturn(page);

        Page<Order> result = controller.list(OrderStatus.PAID, 1, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(orderRepository).findByStatus(eq(OrderStatus.PAID), any(PageRequest.class));
    }

    @Test
    void markPaidCallsOrderService() {
        MarkPaidRequest request = new MarkPaidRequest();
        request.setOrderNo("ORD-1");
        request.setGateway("RAZORPAY");
        request.setPaymentRef("pay_123");

        Order order = new Order();
        when(orderService.markPaid("ORD-1", "RAZORPAY", "pay_123")).thenReturn(order);

        Order result = controller.markPaid(request);

        assertThat(result).isSameAs(order);
        verify(orderService).markPaid("ORD-1", "RAZORPAY", "pay_123");
    }

    @Test
    void cancelCallsOrderService() {
        CancelRequest request = new CancelRequest();
        request.setOrderNo("ORD-2");
        request.setReason("Duplicates");

        Order order = new Order();
        when(orderService.cancel("ORD-2", "Duplicates")).thenReturn(order);

        Order result = controller.cancel(request);

        assertThat(result).isSameAs(order);
        verify(orderService).cancel("ORD-2", "Duplicates");
    }

    @Test
    void addNoteUsesActorFromHeaderOrFallback() {
        OrderNote note = new OrderNote();
        when(noteService.add("ORD-3", "Packed", "system")).thenReturn(note);

        OrderNote result = controller.addNote("ORD-3", "Packed", null);

        assertThat(result).isSameAs(note);
        verify(noteService).add("ORD-3", "Packed", "system");
    }

    @Test
    void listNotesReturnsNotesFromService() {
        OrderNote note = new OrderNote();
        when(noteService.list("ORD-4")).thenReturn(List.of(note));

        List<OrderNote> result = controller.listNotes("ORD-4");

        assertThat(result).containsExactly(note);
    }
}
