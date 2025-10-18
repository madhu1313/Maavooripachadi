package com.maavooripachadi.order;

import org.springframework.stereotype.Service;

/**
 * Lightweight pricing helper used within the order module for subtotal adjustments.
 */
@Service
public class OrderPricingService {

  public int shippingPaiseForPincode(String pincode) {
    return 5_000; // Rs 50 flat stub
  }

  public int taxPaiseOnSubtotal(int subtotalPaise) {
    return Math.round(subtotalPaise * 0.05f); // 5% stub
  }

  public int discountPaise(String couponCode, int subtotalPaise) {
    return (couponCode != null && !couponCode.isBlank()) ? Math.min(1_000, subtotalPaise / 10) : 0;
  }
}
