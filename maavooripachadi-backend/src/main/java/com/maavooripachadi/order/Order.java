package com.maavooripachadi.order;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders", indexes = { @Index(name = "ix_order_no", columnList = "order_no", unique = true), @Index(name = "ix_order_status", columnList = "status") })
public class Order extends BaseEntity {


  @Column(name = "order_no", nullable = false, unique = true)
  private String orderNo; // e.g., ORD-20251005-000123


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status = OrderStatus.DRAFT;


  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false)
  private PaymentStatus paymentStatus = PaymentStatus.PENDING;


  // money in paise
  @Column(nullable = false)
  private int subtotalPaise;
  @Column(nullable = false)
  private int shippingPaise;
  @Column(nullable = false)
  private int discountPaise;
  @Column(nullable = false)
  private int taxPaise;
  @Column(nullable = false)
  private int totalPaise;


  private String currency = "INR";


  private String customerEmail;
  private String customerPhone;
  private String customerName;


  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "name", column = @Column(name = "ship_name")),
    @AttributeOverride(name = "phone", column = @Column(name = "ship_phone")),
    @AttributeOverride(name = "line1", column = @Column(name = "ship_line1")),
    @AttributeOverride(name = "line2", column = @Column(name = "ship_line2")),
    @AttributeOverride(name = "city", column = @Column(name = "ship_city")),
    @AttributeOverride(name = "state", column = @Column(name = "ship_state")),
    @AttributeOverride(name = "pincode", column = @Column(name = "ship_pincode")),
    @AttributeOverride(name = "country", column = @Column(name = "ship_country"))
  })
  private OrderAddress shipTo = new OrderAddress();
private String notes;
private String couponCode;


private String paymentGateway; // razorpay, etc
private String paymentRef; // gateway order/intent id


private OffsetDateTime paidAt;


@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items = new ArrayList<>();


// getters/setters
public String getOrderNo() { return orderNo; }
public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
public OrderStatus getStatus() { return status; }
public void setStatus(OrderStatus status) { this.status = status; }
public PaymentStatus getPaymentStatus() { return paymentStatus; }
public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
public int getSubtotalPaise() { return subtotalPaise; }
public void setSubtotalPaise(int subtotalPaise) { this.subtotalPaise = subtotalPaise; }
public int getShippingPaise() { return shippingPaise; }
public void setShippingPaise(int shippingPaise) { this.shippingPaise = shippingPaise; }
public int getDiscountPaise() { return discountPaise; }
public void setDiscountPaise(int discountPaise) { this.discountPaise = discountPaise; }
public int getTaxPaise() { return taxPaise; }
public void setTaxPaise(int taxPaise) { this.taxPaise = taxPaise; }
public int getTotalPaise() { return totalPaise; }
public void setTotalPaise(int totalPaise) { this.totalPaise = totalPaise; }
public String getCurrency() { return currency; }
public void setCurrency(String currency) { this.currency = currency; }
public String getCustomerEmail() { return customerEmail; }
public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
public String getCustomerPhone() { return customerPhone; }
public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
public String getCustomerName() { return customerName; }
public void setCustomerName(String customerName) { this.customerName = customerName; }
public OrderAddress getShipTo() { return shipTo; }
public void setShipTo(OrderAddress shipTo) { this.shipTo = shipTo; }
public String getNotes() { return notes; }
public void setNotes(String notes) { this.notes = notes; }
public String getCouponCode() { return couponCode; }
public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
public String getPaymentGateway() { return paymentGateway; }
public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }
public String getPaymentRef() { return paymentRef; }
public void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }
public OffsetDateTime getPaidAt() { return paidAt; }
public void setPaidAt(OffsetDateTime paidAt) { this.paidAt = paidAt; }
public List<OrderItem> getItems() { return items; }
public void setItems(List<OrderItem> items) { this.items = items; }
}
