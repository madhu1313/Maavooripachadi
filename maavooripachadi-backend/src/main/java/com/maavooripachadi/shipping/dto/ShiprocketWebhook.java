package com.maavooripachadi.shipping.dto;

public class ShiprocketWebhook {
    private String orderId; private String awb; private String currentStatus; private String currentScan; private String location; private String trackUrl; private String eventTime;
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getAwb() { return awb; }
    public void setAwb(String awb) { this.awb = awb; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public String getCurrentScan() { return currentScan; }
    public void setCurrentScan(String currentScan) { this.currentScan = currentScan; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getTrackUrl() { return trackUrl; }
    public void setTrackUrl(String trackUrl) { this.trackUrl = trackUrl; }
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
}
