package com.maavooripachadi.pricing.dto;


import jakarta.validation.constraints.NotBlank;


public class SetRateRequest {
    @NotBlank private String from;
    @NotBlank private String to;
    private double rate;
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }
}