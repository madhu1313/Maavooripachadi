package com.maavooripachadi.metrics;


import com.maavooripachadi.metrics.dto.EventRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Map;


@Service
public class MetricsService {
    private final MetricEventRepository events;
    private final MetricCounterRepository counters;


    public MetricsService(MetricEventRepository events, MetricCounterRepository counters){
        this.events = events; this.counters = counters;
    }


    @Transactional
    public MetricEvent record(EventRequest req){
        MetricEvent e = new MetricEvent();
        e.setName(req.getName());
        e.setUnit(req.getUnit());
        e.setValue(req.getValue() == null ? 1.0 : req.getValue());
        e.setTagsJson(req.getTagsJson());
        e.setOccurredAt(parseOrNow(req.getOccurredAt()));
        return events.save(e);
    }


    @Transactional(readOnly = true)
    public Page<MetricEvent> window(String name, OffsetDateTime from, OffsetDateTime to, int page, int size){
        return events.findByNameAndWindow(name, from, to, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "occurredAt")));
    }


    private OffsetDateTime parseOrNow(String iso){
        if (iso == null || iso.isBlank()) return OffsetDateTime.now(ZoneOffset.UTC);
        try { return OffsetDateTime.parse(iso); } catch (DateTimeParseException ex) { return OffsetDateTime.now(ZoneOffset.UTC); }
    }
}