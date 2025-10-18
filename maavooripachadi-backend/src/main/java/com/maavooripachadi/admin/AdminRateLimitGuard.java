package com.maavooripachadi.admin;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Minimal in‑memory rate guard (endpoint + actor key). Replace with Bucket4j/
 Redis in prod.
 */
@Component
public class AdminRateLimitGuard {
    private static final int WINDOW_SECONDS = 5;
    private static final int MAX_CALLS = 10;

    private static final class Cell {
        int count;
        long windowStart;
    }

    private final Map<String, Cell> buckets = new ConcurrentHashMap<>();

    public void check(String endpoint, String actor) {
        String key = (actor == null ? "anon" : actor) + "|" + endpoint;
        long nowSec = System.currentTimeMillis() / 1000L;
        var cell = buckets.computeIfAbsent(key, k -> {
            var c = new Cell();
            c.windowStart = nowSec;
            return c;
        });
        if (nowSec - cell.windowStart >= WINDOW_SECONDS) {
            cell.windowStart =
                    nowSec;
            cell.count = 0;
        }
        if (++cell.count > MAX_CALLS) {
            throw new RateLimitException("Too many requests to " + endpoint);
        }
    }

    public static class RateLimitException extends RuntimeException {
        public RateLimitException(String m) {
            super(m);
        }
    }
}