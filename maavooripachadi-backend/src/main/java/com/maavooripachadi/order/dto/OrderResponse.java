package com.maavooripachadi.order.dto;


import com.maavooripachadi.order.*;
import java.util.List;


public class OrderResponse {
    private String orderNo; private OrderStatus status; private PaymentStatus paymentStatus; private int totalPaise; private String currency; private List<OrderItemResponse> items;
    public String getOrderNo() { return orderNo; } public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public OrderStatus getStatus() { return status; } public void setStatus(OrderStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; } public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public int getTotalPaise() { return totalPaise; } public void setTotalPaise(int totalPaise) { this.totalPaise = totalPaise; }
    public String getCurrency() { return currency; } public void setCurrency(String currency) { this.currency = currency; }
    public List<OrderItemResponse> getItems() { return items; } public void setItems(List<OrderItemResponse> items) { this.items = items; }


    public static OrderResponse from(Order o){
        OrderResponse r = new OrderResponse();
        r.setOrderNo(o.getOrderNo()); r.setStatus(o.getStatus()); r.setPaymentStatus(o.getPaymentStatus()); r.setTotalPaise(o.getTotalPaise()); r.setCurrency(o.getCurrency());
        java.util.List<OrderItemResponse> list = new java.util.ArrayList<>();
        for (OrderItem it : o.getItems()) list.add(OrderItemResponse.from(it));
        r.setItems(list); return r;
    }
}