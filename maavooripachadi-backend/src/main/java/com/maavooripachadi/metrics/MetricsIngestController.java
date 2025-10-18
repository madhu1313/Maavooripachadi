package com.maavooripachadi.metrics;


import com.maavooripachadi.metrics.dto.EventRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/metrics")
@Validated
public class MetricsIngestController {
    private final MetricsService service;
    public MetricsIngestController(MetricsService service){ this.service = service; }


    @PostMapping("/event")
    @ResponseStatus(HttpStatus.CREATED)
    public MetricEvent record(@Valid @RequestBody EventRequest req){
        return service.record(req);
    }
}