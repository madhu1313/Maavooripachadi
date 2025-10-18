package com.maavooripachadi.payments.recovery;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "payments.recovery")
public class RecoveryProperties {
    /** Token validity window in hours (default 6). */
    private int ttlHours = 6;
    /** Base URL to build recovery deep links, e.g., https://maavooripachadi.com */
    private String linkBase = "/";


    public int getTtlHours() { return ttlHours; }
    public void setTtlHours(int ttlHours) { this.ttlHours = ttlHours; }
    public String getLinkBase() { return linkBase; }
    public void setLinkBase(String linkBase) { this.linkBase = linkBase; }
}