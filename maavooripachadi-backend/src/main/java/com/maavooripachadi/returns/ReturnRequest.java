package com.maavooripachadi.returns;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "return_request", indexes = {
        @Index(name = "ix_rtn_order", columnList = "order_no"),
        @Index(name = "ix_rtn_status", columnList = "status")
})
public class ReturnRequest extends BaseEntity {


  @Column(name = "order_no", nullable = false, length = 64)
  private String orderNo;


@Enumerated(EnumType.STRING)
@Column(nullable = false)
private ReturnStatus status = ReturnStatus.OPEN;


@Enumerated(EnumType.STRING)
@Column(nullable = false)
private RefundMethod refundMethod = RefundMethod.ORIGINAL_PAYMENT;


@Column(length = 64)
private String rmaCode; // generated upon approval


private OffsetDateTime approvedAt;
private OffsetDateTime receivedAt;
private OffsetDateTime closedAt;


@Column(length = 255)
private String customerEmail;


@OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
private List<ReturnItem> items = new ArrayList<>();


@Lob
private String notes; // freeform customer/admin notes


// getters & setters
public String getOrderNo() { return orderNo; }
public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
public ReturnStatus getStatus() { return status; }
public void setStatus(ReturnStatus status) { this.status = status; }
public RefundMethod getRefundMethod() { return refundMethod; }
public void setRefundMethod(RefundMethod refundMethod) { this.refundMethod = refundMethod; }
public String getRmaCode() { return rmaCode; }
public void setRmaCode(String rmaCode) { this.rmaCode = rmaCode; }
public OffsetDateTime getApprovedAt() { return approvedAt; }
public void setApprovedAt(OffsetDateTime approvedAt) { this.approvedAt = approvedAt; }
public OffsetDateTime getReceivedAt() { return receivedAt; }
public void setReceivedAt(OffsetDateTime receivedAt) { this.receivedAt = receivedAt; }
public OffsetDateTime getClosedAt() { return closedAt; }
public void setClosedAt(OffsetDateTime closedAt) { this.closedAt = closedAt; }
public String getCustomerEmail() { return customerEmail; }
public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
public List<ReturnItem> getItems() { return items; }
public void setItems(List<ReturnItem> items) { this.items = items; }
public String getNotes() { return notes; }
public void setNotes(String notes) { this.notes = notes; }
}