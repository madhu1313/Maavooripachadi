package com.maavooripachadi.payments.gateway;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "payment_attempt", indexes = {
        @Index(name = "ix_pay_order", columnList = "order_no"),
        @Index(name = "ix_pay_status", columnList = "status")
})
public class PaymentAttempt extends BaseEntity {


    @Column(name = "order_no", nullable = false)
    private String orderNo; // business order number


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatewayName gateway = GatewayName.RAZORPAY;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.CREATED;


    @Column(name = "amount_paise", nullable = false)
    private int amountPaise;


    @Column(name = "currency", nullable = false)
    private String currency = "INR";


    @Column(name = "gateway_order_id")
    private String gatewayOrderId; // provider order id
    @Column(name = "gateway_payment_id")
    private String gatewayPaymentId; // provider payment id
    @Column(name = "gateway_signature")
    private String gatewaySignature; // signature from provider


    @Lob
    @Column(name = "meta_json")
    private String metaJson; // arbitrary metadata


    // getters/setters
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public GatewayName getGateway() { return gateway; }
    public void setGateway(GatewayName gateway) { this.gateway = gateway; }
    public AttemptStatus getStatus() { return status; }
    public void setStatus(AttemptStatus status) { this.status = status; }
    public int getAmountPaise() { return amountPaise; }
    public void setAmountPaise(int amountPaise) { this.amountPaise = amountPaise; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getGatewayOrderId() { return gatewayOrderId; }
    public void setGatewayOrderId(String gatewayOrderId) { this.gatewayOrderId = gatewayOrderId; }
    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }
    public String getGatewaySignature() { return gatewaySignature; }
    public void setGatewaySignature(String gatewaySignature) { this.gatewaySignature = gatewaySignature; }
    public String getMetaJson() { return metaJson; }
    public void setMetaJson(String metaJson) { this.metaJson = metaJson; }
}
