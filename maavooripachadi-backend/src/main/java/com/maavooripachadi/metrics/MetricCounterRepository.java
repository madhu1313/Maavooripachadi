package com.maavooripachadi.metrics;


import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query;
import java.time.OffsetDateTime;


public interface MetricCounterRepository extends JpaRepository<MetricCounter, Long> {
    @Query("SELECT c FROM MetricCounter c WHERE c.name = :name AND c.granularity = :g AND c.windowStart >= :from AND c.windowEnd <= :to ORDER BY c.windowStart")
    Page<MetricCounter> findSeries(String name, MetricGranularity g, OffsetDateTime from, OffsetDateTime to, Pageable pageable);
}