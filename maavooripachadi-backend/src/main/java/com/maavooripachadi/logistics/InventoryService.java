package com.maavooripachadi.logistics;


import com.maavooripachadi.logistics.dto.ReserveRequest;
import com.maavooripachadi.logistics.dto.AdjustInventoryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class InventoryService {
    private final InventoryRepository invRepo;
    private final WarehouseRepository whRepo;
    private final StockMovementRepository movRepo;


    public InventoryService(InventoryRepository invRepo, WarehouseRepository whRepo, StockMovementRepository movRepo){
        this.invRepo = invRepo; this.whRepo = whRepo; this.movRepo = movRepo;
    }


@Transactional
public Inventory adjust(AdjustInventoryRequest req){
    Warehouse wh = whRepo.findByCode(req.getWarehouseCode()).orElseThrow();
    Inventory inv = invRepo.findByWarehouseIdAndVariantId(wh.getId(), req.getVariantId()).orElseGet(() -> createInv(wh, req.getVariantId()));
    int newOnHand = inv.getOnHand() + req.getDeltaOnHand();
    if (newOnHand < 0) throw new IllegalStateException("Insufficient inventory");
    inv.setOnHand(newOnHand);
    invRepo.save(inv);


    StockMovement m = new StockMovement();
    m.setInventory(inv);
    m.setType(MovementType.ADJUST);
    m.setQuantity(Math.abs(req.getDeltaOnHand()));
    m.setReason(req.getReason());
    movRepo.save(m);
    return inv;
}


@Transactional
public void reserve(ReserveRequest req){
    Warehouse wh = whRepo.findByCode(req.getWarehouseCode()).orElseThrow();
    Inventory inv = invRepo.findByWarehouseIdAndVariantId(wh.getId(), req.getVariantId()).orElseThrow();
    int available = inv.getOnHand() - inv.getReserved();
    if (available < req.getQty()) throw new IllegalStateException("Not enough stock to reserve");
    inv.setReserved(inv.getReserved() + req.getQty());
    invRepo.save(inv);
    StockMovement m = new StockMovement();
    m.setInventory(inv); m.setType(MovementType.RESERVE); m.setQuantity(req.getQty()); m.setReason(req.getOrderNo());
    movRepo.save(m);
}


@Transactional
public void unreserve(String warehouseCode, Long variantId, int qty, String reason){
    Warehouse wh = whRepo.findByCode(warehouseCode).orElseThrow();
    Inventory inv = invRepo.findByWarehouseIdAndVariantId(wh.getId(), variantId).orElseThrow();
    int newRes = inv.getReserved() - qty; if (newRes < 0) newRes = 0; inv.setReserved(newRes);
    invRepo.save(inv);
    StockMovement m = new StockMovement();
    m.setInventory(inv); m.setType(MovementType.UNRESERVE); m.setQuantity(qty); m.setReason(reason);
    movRepo.save(m);
}


@Transactional
public void allocateAndShip(String warehouseCode, Long variantId, int qty, String orderNo){
    Warehouse wh = whRepo.findByCode(warehouseCode).orElseThrow();
    Inventory inv = invRepo.findByWarehouseIdAndVariantId(wh.getId(), variantId).orElseThrow();
    if (inv.getReserved() < qty) throw new IllegalStateException("Not reserved to allocate");
    inv.setReserved(inv.getReserved() - qty);
    inv.setOnHand(inv.getOnHand() - qty);
    invRepo.save(inv);
    StockMovement m1 = new StockMovement(); m1.setInventory(inv); m1.setType(MovementType.ALLOCATE); m1.setQuantity(qty); m1.setReason(orderNo); movRepo.save(m1);
    StockMovement m2 = new StockMovement(); m2.setInventory(inv); m2.setType(MovementType.SHIP); m2.setQuantity(qty); m2.setReason(orderNo); movRepo.save(m2);
}


private Inventory createInv(Warehouse wh, Long variantId){
    Inventory i = new Inventory(); i.setWarehouse(wh); i.setVariantId(variantId); i.setOnHand(0); i.setReserved(0); return invRepo.save(i);
}
}