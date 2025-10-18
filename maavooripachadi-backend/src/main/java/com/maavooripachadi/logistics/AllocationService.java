package com.maavooripachadi.logistics;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AllocationService {
  private final WarehouseRepository whRepo;
  private final WarehouseStockRepository wsRepo;

  public Warehouse choose(String pincode) {
    return whRepo.findByActiveTrue().stream().findFirst().orElseThrow();
  }

  @Transactional
  public void reserve(Long variantId, int qty, Warehouse warehouse) {
    var stockRow = wsRepo.findByWarehouse(warehouse).stream()
        .filter(row -> Objects.equals(row.getVariantId(), variantId))
        .findFirst()
        .orElseGet(() -> {
          var newRow = new WarehouseStock();
          newRow.setWarehouse(warehouse);
          newRow.setVariantId(variantId);
          return wsRepo.save(newRow);
        });

    int onHand = stockRow.getOnHand() == null ? 0 : stockRow.getOnHand();
    int reserved = stockRow.getReserved() == null ? 0 : stockRow.getReserved();
    if ((onHand - reserved) < qty) {
      throw new RuntimeException("insufficient");
    }
    stockRow.setReserved(reserved + qty);
    wsRepo.save(stockRow);
  }

  @Transactional
  public void convert(Long variantId, int qty, Warehouse warehouse) {
    var stockRow = wsRepo.findByWarehouse(warehouse).stream()
        .filter(row -> Objects.equals(row.getVariantId(), variantId))
        .findFirst()
        .orElseThrow();
    int reserved = stockRow.getReserved() == null ? 0 : stockRow.getReserved();
    int onHand = stockRow.getOnHand() == null ? 0 : stockRow.getOnHand();
    stockRow.setReserved(reserved - qty);
    stockRow.setOnHand(onHand - qty);
    wsRepo.save(stockRow);
  }

  @Transactional
  public void release(Long variantId, int qty, Warehouse warehouse) {
    var stockRow = wsRepo.findByWarehouse(warehouse).stream()
        .filter(row -> Objects.equals(row.getVariantId(), variantId))
        .findFirst()
        .orElseThrow();
    int reserved = stockRow.getReserved() == null ? 0 : stockRow.getReserved();
    stockRow.setReserved(Math.max(0, reserved - qty));
    wsRepo.save(stockRow);
  }
}
