package com.maavooripachadi.metrics;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.time.OffsetDateTime;


@Component
public class ApiTimingFilter extends OncePerRequestFilter {


    private final ApiMetricsService metrics;


    public ApiTimingFilter(ApiMetricsService metrics){ this.metrics = metrics; }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durMs = (System.nanoTime() - start) / 1_000_000L;
            String path = request.getRequestURI();
            if (path.startsWith("/api")) { // avoid static/assets
                ApiMetric m = new ApiMetric();
                m.setMethod(request.getMethod());
                m.setPath(path);
                m.setStatus(response.getStatus());
                m.setDurationMs(durMs);
                m.setIp(request.getRemoteAddr());
                m.setOccurredAt(OffsetDateTime.now());
                metrics.save(m);
            }
        }
    }
}