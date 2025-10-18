package com.maavooripachadi.payments.settlement;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "settlement_line", indexes = {
        @Index(name = "ix_settle_order", columnList = "order_no"),
        @Index(name = "ix_settle_gateway_payment", columnList = "gateway_payment_id")
})
public class SettlementLine extends BaseEntity {


    @ManyToOne(optional = false)
    private SettlementBatch batch;


    @Column(name = "order_no", nullable = false, length = 64)
    private String orderNo;


    @Column(name = "gateway_payment_id", length = 64)
    private String gatewayPaymentId;


    @Column(nullable = false)
    private int amountPaise;


    private int feePaise;
    private int taxPaise;


    @Column(length = 16)
    private String status; // SETTLED/FAILED


    // getters/setters
    public SettlementBatch getBatch() { return batch; }
    public void setBatch(SettlementBatch batch) { this.batch = batch; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }
    public int getAmountPaise() { return amountPaise; }
    public void setAmountPaise(int amountPaise) { this.amountPaise = amountPaise; }
    public int getFeePaise() { return feePaise; }
    public void setFeePaise(int feePaise) { this.feePaise = feePaise; }
    public int getTaxPaise() { return taxPaise; }
    public void setTaxPaise(int taxPaise) { this.taxPaise = taxPaise; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}