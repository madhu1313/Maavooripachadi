package com.maavooripachadi.payments.gateway;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "payment_refund")
public class PaymentRefund extends BaseEntity {


    @ManyToOne(optional = false)
    private PaymentAttempt attempt;


    @Column(nullable = false)
    private int amountPaise;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status = RefundStatus.REQUESTED;


    private String gatewayRefundId;


    private String reason;


    // getters/setters
    public PaymentAttempt getAttempt() { return attempt; }
    public void setAttempt(PaymentAttempt attempt) { this.attempt = attempt; }
    public int getAmountPaise() { return amountPaise; }
    public void setAmountPaise(int amountPaise) { this.amountPaise = amountPaise; }
    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }
    public String getGatewayRefundId() { return gatewayRefundId; }
    public void setGatewayRefundId(String gatewayRefundId) { this.gatewayRefundId = gatewayRefundId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}