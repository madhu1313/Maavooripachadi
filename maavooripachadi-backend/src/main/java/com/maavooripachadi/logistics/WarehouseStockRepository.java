package com.maavooripachadi.logistics;
import org.springframework.data.jpa.repository.*;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock,Long>{
    java.util.List<WarehouseStock> findByWarehouse(Warehouse w);
    java.util.List<WarehouseStock> findByVariantId(Long variantId); }
