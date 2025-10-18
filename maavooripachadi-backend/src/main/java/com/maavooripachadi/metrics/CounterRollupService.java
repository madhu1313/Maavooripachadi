package com.maavooripachadi.metrics;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;


@Service
public class CounterRollupService {
    private final MetricEventRepository events;
    private final MetricCounterRepository counters;


    public CounterRollupService(MetricEventRepository events, MetricCounterRepository counters){ this.events = events; this.counters = counters; }


    /** Roll up events of the previous minute into MINUTE counters. */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void rollupMinute(){
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).withSecond(0).withNano(0);
        OffsetDateTime from = now.minusMinutes(1);
        OffsetDateTime to = now;
// naive rollup: load a page large enough (tune for prod)
        var page = events.findByNameAndWindow("checkout_start", from, to, org.springframework.data.domain.PageRequest.of(0, 10000));
        aggregate(page.getContent(), from, to);
        page = events.findByNameAndWindow("payment_success", from, to, org.springframework.data.domain.PageRequest.of(0, 10000));
        aggregate(page.getContent(), from, to);
// TODO: add other event names or make list configurable
    }


    private void aggregate(List<MetricEvent> list, OffsetDateTime from, OffsetDateTime to){
        if (list.isEmpty()) return;
        String name = list.get(0).getName();
        MetricCounter c = new MetricCounter();
        c.setName(name); c.setGranularity(MetricGranularity.MINUTE); c.setWindowStart(from); c.setWindowEnd(to);
        c.setCount(list.size());
        double sum = 0; Double min = null, max = null;
        for (MetricEvent e : list){
            sum += e.getValue();
            min = (min == null) ? e.getValue() : Math.min(min, e.getValue());
            max = (max == null) ? e.getValue() : Math.max(max, e.getValue());
        }
        c.setSum(sum); c.setMinVal(min); c.setMaxVal(max);
        counters.save(c);
    }
}