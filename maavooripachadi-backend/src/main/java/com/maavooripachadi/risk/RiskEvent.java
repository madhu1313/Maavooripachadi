package com.maavooripachadi.risk;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "risk_event", indexes = {
        @Index(name = "ix_risk_evt_source", columnList = "source"),
        @Index(name = "ix_risk_evt_ip", columnList = "ip"),
        @Index(name = "ix_risk_evt_email", columnList = "email")
})
public class RiskEvent extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskSource source; // CHECKOUT/PAYMENT/LOGIN

    private String subjectId; // user id/email
    private String email;     // copy for quick queries
    private String phone;

    private String ip;
    private String deviceId; // fingerprint

    private String orderNo;
    private Integer amountPaise;
    private String currency;

    @Lob
    private String payloadJson; // raw request context

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
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}
