package com.maavooripachadi.metrics;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;


@Service
public class ApiMetricsService {
    private final ApiMetricRepository repo;


    public ApiMetricsService(ApiMetricRepository repo){ this.repo = repo; }


    @Transactional
    public ApiMetric save(ApiMetric m){ return repo.save(m); }


    @Transactional(readOnly = true)
    public Page<ApiMetric> window(OffsetDateTime from, OffsetDateTime to, String path, int page, int size){
        return repo.findWindow(from, to, path, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt")));
    }
}