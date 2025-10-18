package com.maavooripachadi.logistics;


import com.maavooripachadi.logistics.dto.CreateShipmentRequest;
import com.maavooripachadi.logistics.dto.LabelRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ShipmentService {
    private final WarehouseRepository whRepo;
    private final ShipmentRepository shipRepo;
    private final PackageItemRepository itemRepo;
    private final InventoryService invService;
    private final CarrierRouterService carrierRouter;


    public ShipmentService(WarehouseRepository whRepo, ShipmentRepository shipRepo, PackageItemRepository itemRepo,
                           InventoryService invService, CarrierRouterService carrierRouter){
        this.whRepo = whRepo; this.shipRepo = shipRepo; this.itemRepo = itemRepo; this.invService = invService; this.carrierRouter = carrierRouter;
    }
@Transactional
public Shipment create(CreateShipmentRequest req){
    var wh = whRepo.findByCode(req.getWarehouseCode()).orElseThrow();
    Shipment s = new Shipment();
    s.setOrderNo(req.getOrderNo());
    s.setShipmentNo("SHP-" + System.currentTimeMillis());
    s.setWarehouse(wh);
    s.setCarrier("SHIPROCKET");
    s.setConsigneeName(req.getConsigneeName()); s.setConsigneePhone(req.getConsigneePhone());
    s.setShipLine1(req.getShipLine1()); s.setShipLine2(req.getShipLine2());
    s.setShipCity(req.getShipCity()); s.setShipState(req.getShipState()); s.setShipPincode(req.getShipPincode()); s.setShipCountry(req.getShipCountry());
    s.setWeightGrams(req.getWeightGrams()); s.setLengthCm(req.getLengthCm()); s.setWidthCm(req.getWidthCm()); s.setHeightCm(req.getHeightCm());
    shipRepo.save(s);
    for (CreateShipmentRequest.Item it : req.getItems()){
        PackageItem pi = new PackageItem();
        pi.setShipment(s); pi.setVariantId(it.getVariantId()); pi.setSku(it.getSku()); pi.setQty(it.getQty());
        itemRepo.save(pi);
        invService.allocateAndShip(req.getWarehouseCode(), it.getVariantId(), it.getQty(), req.getOrderNo());
    }
    return s;
}


@Transactional
public Shipment buyLabel(LabelRequest req){
    Shipment s = shipRepo.findByShipmentNo(req.getShipmentNo()).orElseThrow();
    s.setCarrier(req.getCarrier());
    var res = carrierRouter.buyLabel(s);
    s.setLabelUrl(res.labelUrl);
    s.setTrackingNo(res.trackingNo);
    s.setStatus(ShipmentStatus.LABEL_CREATED);
    return shipRepo.save(s);
}


@Transactional(readOnly = true)
public Page<Shipment> listByStatus(ShipmentStatus status, int page, int size){
    return shipRepo.findByStatus(status, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
}


@Transactional
public Shipment updateTracking(String carrier, String payload){
    var ev = carrierRouter.parseWebhook(carrier, payload);
    var s = shipRepo.findAll().stream().filter(x -> ev.trackingNo.equals(x.getTrackingNo())).findFirst().orElse(null);
    if (s == null) return null;
    if ("DELIVERED".equalsIgnoreCase(ev.status)) s.setStatus(ShipmentStatus.DELIVERED);
    else if ("IN_TRANSIT".equalsIgnoreCase(ev.status)) s.setStatus(ShipmentStatus.IN_TRANSIT);
    else if ("RETURNED".equalsIgnoreCase(ev.status)) s.setStatus(ShipmentStatus.RETURNED);
    return shipRepo.save(s);
}
}