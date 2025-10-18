package com.maavooripachadi.returns;


public interface PaymentsPort {
    /** Perform refund for an order number. Returns provider refund id or reference. */
    String refund(String orderNo, int amountPaise, String reason);
}