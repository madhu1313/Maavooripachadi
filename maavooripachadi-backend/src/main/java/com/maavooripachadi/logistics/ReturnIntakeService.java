package com.maavooripachadi.logistics;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReturnIntakeService {
  private final WarehouseRepository whRepo;
  private final WarehouseStockRepository stockRepo;
  private final ReturnIntakeRepository repo;

  @Transactional
  public void process(long returnId,
                      long warehouseId,
                      String condition,
                      boolean restockable,
                      long variantId,
                      int qty,
                      String actor) {
    var intake = new ReturnIntake();
    intake.setReturnId(returnId);
    intake.setWarehouseId(warehouseId);
    intake.setCondition(condition);
    intake.setRestockable(restockable);
    intake.setVariantId(variantId);
    intake.setQty(qty);
    intake.setProcessedBy(actor);
    repo.save(intake);

    if (restockable) {
      var warehouse = whRepo.findById(warehouseId).orElseThrow();
      var stockRow = stockRepo.findByWarehouse(warehouse).stream()
          .filter(row -> Long.valueOf(variantId).equals(row.getVariantId()))
          .findFirst()
          .orElseGet(() -> {
            var newRow = new WarehouseStock();
            newRow.setWarehouse(warehouse);
            newRow.setVariantId(variantId);
            return stockRepo.save(newRow);
          });
      int currentOnHand = stockRow.getOnHand() == null ? 0 : stockRow.getOnHand();
      stockRow.setOnHand(currentOnHand + qty);
      stockRepo.save(stockRow);
    }
  }
}

