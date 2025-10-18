package com.maavooripachadi.order;


import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNo(String orderNo);
    Page<Order> findByStatus(OrderStatus st, Pageable pageable);
    Optional<Order> findFirstByOrderNoStartingWithOrderByOrderNoDesc(String prefix);
}
