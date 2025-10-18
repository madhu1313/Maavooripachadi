package com.maavooripachadi.metrics;


import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;


public interface MetricEventRepository extends JpaRepository<MetricEvent, Long> {
    @Query("SELECT e FROM MetricEvent e WHERE e.name = :name AND e.occurredAt >= :from AND e.occurredAt < :to")
    Page<MetricEvent> findByNameAndWindow(@Param("name") String name,
                                          @Param("from") OffsetDateTime from,
                                          @Param("to") OffsetDateTime to,
                                          Pageable pageable);
}
