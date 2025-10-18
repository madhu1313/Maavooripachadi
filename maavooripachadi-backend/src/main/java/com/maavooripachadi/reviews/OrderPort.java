package com.maavooripachadi.reviews;


public interface OrderPort {
    /** Return true if subject has purchased product/variant (eligible for verified review). */
    boolean hasPurchased(String subjectId, Long productId, Long variantId);
}