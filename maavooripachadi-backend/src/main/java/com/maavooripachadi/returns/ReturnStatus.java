package com.maavooripachadi.returns;
public enum ReturnStatus {
    OPEN, // created by customer, awaiting review
    APPROVED, // RMA issued
    REJECTED,
    IN_TRANSIT, // customer shipped back
    RECEIVED, // warehouse received
    PARTIAL_RECEIVED,
    REFUNDED, // refund completed
    EXCHANGED, // exchange shipped
    CLOSED // terminal (no further action)
}