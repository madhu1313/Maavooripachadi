package com.maavooripachadi.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "support")
public class SupportProperties {
    private String ticketPrefix = "ST";
    private int ticketDigits = 6;
    private String defaultSlaName = "Default";

    public String getTicketPrefix() { return ticketPrefix; }
    public void setTicketPrefix(String ticketPrefix) { this.ticketPrefix = ticketPrefix; }
    public int getTicketDigits() { return ticketDigits; }
    public void setTicketDigits(int ticketDigits) { this.ticketDigits = ticketDigits; }
    public String getDefaultSlaName() { return defaultSlaName; }
    public void setDefaultSlaName(String defaultSlaName) { this.defaultSlaName = defaultSlaName; }
}
