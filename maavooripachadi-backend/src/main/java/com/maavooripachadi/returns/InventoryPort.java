package com.maavooripachadi.returns;


public interface InventoryPort {
    /** Increase on-hand qty for a variant (restock). */
    void incrementOnHand(Long variantId, int qty);
}