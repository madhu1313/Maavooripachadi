package com.maavooripachadi.order;


public enum OrderStatus {
    DRAFT, // created but not confirmed
    PENDING, // confirmed, awaiting payment
    PAID, // payment captured
    PACKED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}