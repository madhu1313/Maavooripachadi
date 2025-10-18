package com.maavooripachadi.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final SecurityProperties props;
    private final Map<String, Window> buckets = new ConcurrentHashMap<>();
    public RateLimitService(SecurityProperties props){ this.props = props; }

    private static class Window { long resetAt; int count; }

    public synchronized boolean allow(String key){
        long now = Instant.now().getEpochSecond();
        int win = props.getRateWindowSeconds();
        Window w = buckets.computeIfAbsent(key, k -> { Window x = new Window(); x.resetAt = now + win; x.count = 0; return x; });
        if (now > w.resetAt){ w.resetAt = now + win; w.count = 0; }
        if (w.count + 1 > props.getRateMaxCalls()) return false;
        w.count++;
        return true;
    }
}
