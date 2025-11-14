package com.maavooripachadi.order;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.orders.notifications")
public class OrderNotificationProperties {

    private boolean emailEnabled = true;
    private boolean whatsappEnabled = true;
    private String fromEmail = "orders@maavooripachadi.com";
    private String opsDisplayName = "Kitchen Ops";
    private String supportWhatsappLink = "https://wa.me/918555859667";
    private List<String> ownerEmails = new ArrayList<>();
    private List<String> ownerWhatsappNumbers = new ArrayList<>();

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isWhatsappEnabled() {
        return whatsappEnabled;
    }

    public void setWhatsappEnabled(boolean whatsappEnabled) {
        this.whatsappEnabled = whatsappEnabled;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getOpsDisplayName() {
        return opsDisplayName;
    }

    public void setOpsDisplayName(String opsDisplayName) {
        this.opsDisplayName = opsDisplayName;
    }

    public String getSupportWhatsappLink() {
        return supportWhatsappLink;
    }

    public void setSupportWhatsappLink(String supportWhatsappLink) {
        this.supportWhatsappLink = supportWhatsappLink;
    }

    public List<String> getOwnerEmails() {
        return ownerEmails;
    }

    public void setOwnerEmails(List<String> ownerEmails) {
        this.ownerEmails = sanitize(ownerEmails);
    }

    public List<String> getOwnerWhatsappNumbers() {
        return ownerWhatsappNumbers;
    }

    public void setOwnerWhatsappNumbers(List<String> ownerWhatsappNumbers) {
        this.ownerWhatsappNumbers = sanitize(ownerWhatsappNumbers);
    }

    private List<String> sanitize(List<String> input) {
        List<String> cleaned = new ArrayList<>();
        if (input == null) {
            return cleaned;
        }
        for (String value : input) {
            if (value != null) {
                String trimmed = value.trim();
                if (!trimmed.isEmpty()) {
                    cleaned.add(trimmed);
                }
            }
        }
        return cleaned;
    }
}
