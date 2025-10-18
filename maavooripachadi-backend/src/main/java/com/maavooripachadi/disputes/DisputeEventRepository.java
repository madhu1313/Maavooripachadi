package com.maavooripachadi.disputes;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface DisputeEventRepository extends JpaRepository<DisputeEvent, Long> {
    List<DisputeEvent> findByDisputeIdOrderByCreatedAtAsc(Long disputeId);
}