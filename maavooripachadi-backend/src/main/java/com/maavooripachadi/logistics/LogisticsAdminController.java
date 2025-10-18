package com.maavooripachadi.logistics;


import com.maavooripachadi.logistics.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/logistics")
@Validated
public class LogisticsAdminController {
    private final InventoryService inventory;
    private final ShipmentService shipments;
    private final WarehouseRepository warehouses;


    public LogisticsAdminController(InventoryService inventory, ShipmentService shipments, WarehouseRepository warehouses){
        this.inventory = inventory; this.shipments = shipments; this.warehouses = warehouses;
    }


// --- Warehouses ---
@PostMapping("/warehouses")
@ResponseStatus(HttpStatus.CREATED)
@PreAuthorize("hasAuthority('LOGISTICS_WRITE') or hasRole('ADMIN')")
public Warehouse upsertWarehouse(@RequestBody Warehouse req){
    if (req.getCode() == null || req.getCode().isBlank()) throw new IllegalArgumentException("code required");
    var existing = warehouses.findByCode(req.getCode()).orElse(null);
    Warehouse w = existing == null ? new Warehouse() : existing;
    w.setCode(req.getCode()); w.setName(req.getName()); w.setLine1(req.getLine1()); w.setLine2(req.getLine2()); w.setCity(req.getCity()); w.setState(req.getState()); w.setPincode(req.getPincode()); w.setCountry(req.getCountry()); w.setActive(req.getActive());
    return warehouses.save(w);
}


@GetMapping("/warehouses")
@PreAuthorize("hasAuthority('LOGISTICS_READ') or hasRole('ADMIN')")
public java.util.List<Warehouse> listWarehouses(){ return warehouses.findAll(); }


// --- Inventory ---
@PostMapping("/inventory/adjust")
@PreAuthorize("hasAuthority('LOGISTICS_WRITE') or hasRole('ADMIN')")
public Inventory adjust(@Valid @RequestBody AdjustInventoryRequest req){ return inventory.adjust(req); }


@PostMapping("/inventory/reserve")
@PreAuthorize("hasAuthority('LOGISTICS_WRITE') or hasRole('ADMIN')")
public java.util.Map<String,Object> reserve(@Valid @RequestBody ReserveRequest req){ inventory.reserve(req); return java.util.Map.of("ok", true); }


@PostMapping("/inventory/unreserve")
@PreAuthorize("hasAuthority('LOGISTICS_WRITE') or hasRole('ADMIN')")
public java.util.Map<String,Object> unreserve(@RequestParam(value = "warehouseCode") String warehouseCode,
                                              @RequestParam(value = "variantId") Long variantId,
                                              @RequestParam(value = "qty") int qty,
                                              @RequestParam(value = "reason") String reason){ inventory.unreserve(warehouseCode, variantId, qty, reason); return java.util.Map.of("ok", true); }


// --- Shipments ---
@PostMapping("/shipments")
@ResponseStatus(HttpStatus.CREATED)
@PreAuthorize("hasAuthority('LOGISTICS_WRITE') or hasRole('ADMIN')")
public Shipment createShipment(@Valid @RequestBody CreateShipmentRequest req){ return shipments.create(req); }


@PostMapping("/shipments/label")
@PreAuthorize("hasAuthority('LOGISTICS_WRITE') or hasRole('ADMIN')")
public Shipment label(@Valid @RequestBody LabelRequest req){ return shipments.buyLabel(req); }


@GetMapping("/shipments")
@PreAuthorize("hasAuthority('LOGISTICS_READ') or hasRole('ADMIN')")
public Page<Shipment> list(@RequestParam(value = "status") ShipmentStatus status,
                           @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
                           @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(200) int size){
    return shipments.listByStatus(status, page, size);
}
}
