package com.maavooripachadi.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private static final List<String> DEFAULT_ALLOWED_ORIGINS = List.of("http://localhost:4200");

    private String jwtSecret = "change-me";
    private long accessTtlSeconds = 3600; // 1h
    private long refreshTtlSeconds = 2592000; // 30d
    private int rateWindowSeconds = 60; // per minute
    private int rateMaxCalls = 120; // default
    private List<String> allowedOrigins = new ArrayList<>(DEFAULT_ALLOWED_ORIGINS);

    public String getJwtSecret() { return jwtSecret; }
    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
    public long getAccessTtlSeconds() { return accessTtlSeconds; }
    public void setAccessTtlSeconds(long accessTtlSeconds) { this.accessTtlSeconds = accessTtlSeconds; }
    public long getRefreshTtlSeconds() { return refreshTtlSeconds; }
    public void setRefreshTtlSeconds(long refreshTtlSeconds) { this.refreshTtlSeconds = refreshTtlSeconds; }
    public int getRateWindowSeconds() { return rateWindowSeconds; }
    public void setRateWindowSeconds(int rateWindowSeconds) { this.rateWindowSeconds = rateWindowSeconds; }
    public int getRateMaxCalls() { return rateMaxCalls; }
    public void setRateMaxCalls(int rateMaxCalls) { this.rateMaxCalls = rateMaxCalls; }

    public List<String> getAllowedOrigins() { return allowedOrigins; }
    public void setAllowedOrigins(List<String> allowedOrigins) {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            this.allowedOrigins = new ArrayList<>(DEFAULT_ALLOWED_ORIGINS);
        } else {
            this.allowedOrigins = new ArrayList<>(allowedOrigins);
        }
    }
}
