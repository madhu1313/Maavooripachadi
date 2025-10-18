package com.maavooripachadi.logistics;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByWarehouseIdAndVariantId(Long warehouseId, Long variantId);
    @Query("select i from Inventory i where i.variantId = :variantId and i.onHand - i.reserved > 0")
    List<Inventory> findAnyAvailable(Long variantId);
}