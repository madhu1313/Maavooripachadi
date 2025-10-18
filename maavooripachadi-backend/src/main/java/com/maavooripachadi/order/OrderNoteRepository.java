package com.maavooripachadi.order;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {
    java.util.List<OrderNote> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}