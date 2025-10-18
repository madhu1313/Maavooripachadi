package com.maavooripachadi.disputes;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;


@Entity
@Table(name = "dispute")
public class Dispute extends BaseEntity {


    @Column(nullable = false)
    private String gateway; // e.g., RAZORPAY, STRIPE


    @Column(nullable = false)
    private String providerCaseId;
private Long paymentAttemptId; // link to payments.PaymentAttempt (optional)


private String orderNo; // denormalized for quick lookup


@Enumerated(EnumType.STRING)
@Column(nullable = false)
private DisputeStatus status = DisputeStatus.OPEN;


private String reason; // "fraud", "product_not_received", etc


private String type; // "chargeback", "retrieval"


@Column(nullable = false)
private int amountPaise; // disputed amount


private String currency = "INR";


private OffsetDateTime evidenceDueAt; // when gateway expects evidence


private OffsetDateTime decidedAt; // when closed/won/lost


@Lob
private String notes; // internal notes / JSON fields


// ---- getters/setters ----
public String getGateway() { return gateway; }
public void setGateway(String gateway) { this.gateway = gateway; }


public String getProviderCaseId() { return providerCaseId; }
public void setProviderCaseId(String providerCaseId) { this.providerCaseId = providerCaseId; }


public Long getPaymentAttemptId() { return paymentAttemptId; }
public void setPaymentAttemptId(Long paymentAttemptId) { this.paymentAttemptId = paymentAttemptId; }


public String getOrderNo() { return orderNo; }
public void setOrderNo(String orderNo) { this.orderNo = orderNo; }


public DisputeStatus getStatus() { return status; }
public void setStatus(DisputeStatus status) { this.status = status; }


public String getReason() { return reason; }
public void setReason(String reason) { this.reason = reason; }


public String getType() { return type; }
public void setType(String type) { this.type = type; }


public int getAmountPaise() { return amountPaise; }
public void setAmountPaise(int amountPaise) { this.amountPaise = amountPaise; }


public String getCurrency() { return currency; }
public void setCurrency(String currency) { this.currency = currency; }


public OffsetDateTime getEvidenceDueAt() { return evidenceDueAt; }
public void setEvidenceDueAt(OffsetDateTime evidenceDueAt) { this.evidenceDueAt = evidenceDueAt; }


public OffsetDateTime getDecidedAt() { return decidedAt; }
public void setDecidedAt(OffsetDateTime decidedAt) { this.decidedAt = decidedAt; }


public String getNotes() { return notes; }
public void setNotes(String notes) { this.notes = notes; }
}