package com.maavooripachadi.privacy;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "privacy")
public class PrivacyProperties {
    /** Minimum days to retain consent logs (for audit) */
    private int consentRetentionDays = 365 * 2;


    public int getConsentRetentionDays() { return consentRetentionDays; }
    public void setConsentRetentionDays(int consentRetentionDays) { this.consentRetentionDays = consentRetentionDays; }
}