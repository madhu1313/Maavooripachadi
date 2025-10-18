package com.maavooripachadi.returns;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "return_item")
public class ReturnItem extends BaseEntity {


    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id")
    private ReturnRequest request;


    @Column(name = "order_line_id", nullable = false)
    private Long orderLineId; // references order item row id in orders module


    @Column(name = "variant_id", nullable = false)
    private Long variantId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnReason reason = ReturnReason.OTHER;


    @Column(nullable = false)
    private int qty; // requested return qty


    private int receivedQty; // warehouse received


    @Column(name = "refund_paise")
    private Integer refundPaise; // per-line refund calculated


    // getters & setters
    public ReturnRequest getRequest() { return request; }
    public void setRequest(ReturnRequest request) { this.request = request; }
    public Long getOrderLineId() { return orderLineId; }
    public void setOrderLineId(Long orderLineId) { this.orderLineId = orderLineId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public ReturnReason getReason() { return reason; }
    public void setReason(ReturnReason reason) { this.reason = reason; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public int getReceivedQty() { return receivedQty; }
    public void setReceivedQty(int receivedQty) { this.receivedQty = receivedQty; }
    public Integer getRefundPaise() { return refundPaise; }
    public void setRefundPaise(Integer refundPaise) { this.refundPaise = refundPaise; }
}