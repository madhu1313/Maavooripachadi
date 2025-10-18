package com.maavooripachadi.metrics;


import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query;
import java.time.OffsetDateTime;


public interface ApiMetricRepository extends JpaRepository<ApiMetric, Long> {
    @Query("SELECT a FROM ApiMetric a WHERE a.occurredAt >= :from AND a.occurredAt < :to AND (:path IS NULL OR a.path = :path)")
    Page<ApiMetric> findWindow(OffsetDateTime from, OffsetDateTime to, String path, Pageable pageable);
}