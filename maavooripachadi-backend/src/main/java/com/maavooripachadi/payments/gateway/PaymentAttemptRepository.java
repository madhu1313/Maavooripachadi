package com.maavooripachadi.payments.gateway;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {
    Optional<PaymentAttempt> findFirstByOrderNoOrderByCreatedAtDesc(String orderNo);
    Optional<PaymentAttempt> findByGatewayOrderId(String gatewayOrderId);
}