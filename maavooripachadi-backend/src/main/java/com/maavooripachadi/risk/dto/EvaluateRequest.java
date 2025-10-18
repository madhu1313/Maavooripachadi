package com.maavooripachadi.risk.dto;

import com.maavooripachadi.risk.RiskSource;
import jakarta.validation.constraints.*;

public class EvaluateRequest {
    @NotNull private RiskSource source = RiskSource.CHECKOUT;
    private String subjectId; private String email; private String phone;
    private String ip; private String deviceId;
    private String orderNo; private Integer amountPaise; private String currency = "INR";
    private String cardFingerprint; // tokenized PAN hash from gateway if available
    private String payloadJson; // raw context (optional)

    public RiskSource getSource() { return source; }
    public void setSource(RiskSource source) { this.source = source; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Integer getAmountPaise() { return amountPaise; }
    public void setAmountPaise(Integer amountPaise) { this.amountPaise = amountPaise; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCardFingerprint() { return cardFingerprint; }
    public void setCardFingerprint(String cardFingerprint) { this.cardFingerprint = cardFingerprint; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}
