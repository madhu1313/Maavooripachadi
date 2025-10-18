package com.maavooripachadi.order;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderNoteService {
    private final OrderRepository orders; private final OrderNoteRepository notes;
    public OrderNoteService(OrderRepository orders, OrderNoteRepository notes){ this.orders = orders; this.notes = notes; }


    @Transactional
    public OrderNote add(String orderNo, String note, String author){
        Order o = orders.findByOrderNo(orderNo).orElseThrow();
        OrderNote n = new OrderNote(); n.setOrder(o); n.setNote(note); n.setAuthor(author);
        return notes.save(n);
    }


    @Transactional(readOnly = true)
    public java.util.List<OrderNote> list(String orderNo){
        Order o = orders.findByOrderNo(orderNo).orElseThrow();
        return notes.findByOrderIdOrderByCreatedAtAsc(o.getId());
    }
}