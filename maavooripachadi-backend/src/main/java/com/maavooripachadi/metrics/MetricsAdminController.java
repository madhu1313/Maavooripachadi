package com.maavooripachadi.metrics;


import com.maavooripachadi.metrics.dto.ApiQuery;
import com.maavooripachadi.metrics.dto.CounterQuery;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.time.OffsetDateTime;


@RestController
@RequestMapping("/api/v1/admin/metrics")
@Validated
public class MetricsAdminController {
    private final MetricsService metrics;
    private final MetricCounterRepository counters;
    private final ApiMetricsService apiMetrics;


    public MetricsAdminController(MetricsService metrics, MetricCounterRepository counters, ApiMetricsService apiMetrics){
        this.metrics = metrics; this.counters = counters; this.apiMetrics = apiMetrics;
    }


    @GetMapping("/events/{name}")
    @PreAuthorize("hasAuthority('METRICS_READ') or hasRole('ADMIN')")
    public org.springframework.data.domain.Page<MetricEvent> events(@PathVariable String name,
                                                                    @RequestParam(value = "from") String from,
                                                                    @RequestParam(value = "to") String to,
                                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                                    @RequestParam(value = "size", defaultValue = "200") int size){
        return metrics.window(name, OffsetDateTime.parse(from), OffsetDateTime.parse(to), page, size);
    }


    @PostMapping("/counters")
    @PreAuthorize("hasAuthority('METRICS_READ') or hasRole('ADMIN')")
    public Page<MetricCounter> counters(@RequestBody CounterQuery q){
        return counters.findSeries(q.getName(), q.getGranularity(), OffsetDateTime.parse(q.getFrom()), OffsetDateTime.parse(q.getTo()), org.springframework.data.domain.PageRequest.of(q.getPage(), q.getSize()));
    }


    @PostMapping("/api")
    @PreAuthorize("hasAuthority('METRICS_READ') or hasRole('ADMIN')")
    public Page<ApiMetric> apis(@RequestBody ApiQuery q){
        return apiMetrics.window(OffsetDateTime.parse(q.getFrom()), OffsetDateTime.parse(q.getTo()), q.getPath(), q.getPage(), q.getSize());
    }
}
