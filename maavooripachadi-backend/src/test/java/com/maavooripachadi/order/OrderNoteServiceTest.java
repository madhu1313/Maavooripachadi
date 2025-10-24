package com.maavooripachadi.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderNoteServiceTest {

    private OrderRepository orderRepository;
    private OrderNoteRepository noteRepository;
    private OrderNoteService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        noteRepository = mock(OrderNoteRepository.class);
        service = new OrderNoteService(orderRepository, noteRepository);
    }

    @Test
    void addCreatesNoteForOrder() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", 5L);
        when(orderRepository.findByOrderNo("ORD-1")).thenReturn(Optional.of(order));

        OrderNote saved = new OrderNote();
        saved.setNote("Packed");
        when(noteRepository.save(any(OrderNote.class))).thenReturn(saved);

        OrderNote result = service.add("ORD-1", "Packed", "alice");

        assertThat(result).isSameAs(saved);
        verify(noteRepository).save(argThat(note ->
            note.getOrder() == order &&
            note.getNote().equals("Packed") &&
            note.getAuthor().equals("alice")
        ));
    }

    @Test
    void listReturnsNotesOrdered() {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "id", 9L);
        when(orderRepository.findByOrderNo("ORD-9")).thenReturn(Optional.of(order));

        OrderNote note = new OrderNote();
        note.setNote("First");
        when(noteRepository.findByOrderIdOrderByCreatedAtAsc(9L)).thenReturn(List.of(note));

        List<OrderNote> result = service.list("ORD-9");

        assertThat(result).containsExactly(note);
    }
}
