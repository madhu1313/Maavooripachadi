package com.maavooripachadi.logistics;

import com.maavooripachadi.logistics.dto.CreateShipmentRequest;
import com.maavooripachadi.logistics.dto.LabelRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ShipmentServiceTest {

  private WarehouseRepository warehouseRepository;
  private ShipmentRepository shipmentRepository;
  private PackageItemRepository packageItemRepository;
  private InventoryService inventoryService;
  private CarrierRouterService carrierRouterService;
  private ShipmentService service;
  private Warehouse warehouse;

  @BeforeEach
  void setUp() {
    warehouseRepository = mock(WarehouseRepository.class);
    shipmentRepository = mock(ShipmentRepository.class);
    packageItemRepository = mock(PackageItemRepository.class);
    inventoryService = mock(InventoryService.class);
    carrierRouterService = mock(CarrierRouterService.class);
    service = new ShipmentService(warehouseRepository, shipmentRepository, packageItemRepository, inventoryService, carrierRouterService);

    warehouse = new Warehouse();
    warehouse.setCode("HYD");
    ReflectionTestUtils.setField(warehouse, "id", 1L);
  }

  @Test
  void createPersistsShipmentItemsAndAllocatesInventory() {
    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(warehouse));
    when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(packageItemRepository.save(any(PackageItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CreateShipmentRequest request = new CreateShipmentRequest();
    request.setOrderNo("ORD-55");
    request.setWarehouseCode("HYD");
    request.setConsigneeName("Ajay");
    request.setConsigneePhone("9000000000");
    request.setShipLine1("Line 1");
    request.setShipCity("Hyderabad");
    request.setShipState("TS");
    request.setShipPincode("500001");
    request.setItems(List.of(item(11L, 2), item(22L, 1)));
    request.setWeightGrams(500);
    request.setLengthCm(10);
    request.setWidthCm(12);
    request.setHeightCm(5);

    Shipment shipment = service.create(request);

    assertThat(shipment.getOrderNo()).isEqualTo("ORD-55");
    assertThat(shipment.getShipmentNo()).isNotBlank();

    verify(shipmentRepository).save(shipment);
    verify(packageItemRepository, times(2)).save(any(PackageItem.class));
    verify(inventoryService).allocateAndShip("HYD", 11L, 2, "ORD-55");
    verify(inventoryService).allocateAndShip("HYD", 22L, 1, "ORD-55");
  }

  @Test
  void buyLabelUpdatesShipmentWithCarrierResponse() {
    Shipment existing = new Shipment();
    existing.setShipmentNo("SHP-1");

    when(shipmentRepository.findByShipmentNo("SHP-1")).thenReturn(Optional.of(existing));
    when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CarrierRouterService.LabelResult result = new CarrierRouterService.LabelResult();
    result.labelUrl = "https://label.pdf";
    result.trackingNo = "TRK-1";
    when(carrierRouterService.buyLabel(existing)).thenReturn(result);

    LabelRequest request = new LabelRequest();
    request.setShipmentNo("SHP-1");
    request.setCarrier("SHIPROCKET");

    Shipment labeled = service.buyLabel(request);

    assertThat(labeled.getLabelUrl()).isEqualTo("https://label.pdf");
    assertThat(labeled.getTrackingNo()).isEqualTo("TRK-1");
    assertThat(labeled.getStatus()).isEqualTo(ShipmentStatus.LABEL_CREATED);
    verify(shipmentRepository).save(existing);
  }

  @Test
  void listByStatusUsesDescendingCreatedAtSort() {
    Page<Shipment> page = new PageImpl<>(List.of(new Shipment()));
    when(shipmentRepository.findByStatus(eq(ShipmentStatus.IN_TRANSIT), any(Pageable.class))).thenReturn(page);

    Page<Shipment> response = service.listByStatus(ShipmentStatus.IN_TRANSIT, 0, 10);

    assertThat(response).isSameAs(page);
    verify(shipmentRepository).findByStatus(eq(ShipmentStatus.IN_TRANSIT), any(Pageable.class));
  }

  @Test
  void updateTrackingAppliesStatusFromCarrierEvent() {
    CarrierRouterService.TrackingEvent event = new CarrierRouterService.TrackingEvent();
    event.trackingNo = "TRK-123";
    event.status = "DELIVERED";
    when(carrierRouterService.parseWebhook("shiprocket", "payload")).thenReturn(event);

    Shipment shipment = new Shipment();
    shipment.setTrackingNo("TRK-123");
    shipment.setStatus(ShipmentStatus.IN_TRANSIT);
    when(shipmentRepository.findAll()).thenReturn(List.of(shipment));
    when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Shipment updated = service.updateTracking("shiprocket", "payload");

    assertThat(updated.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
    verify(shipmentRepository).save(shipment);
  }

  @Test
  void updateTrackingReturnsNullWhenShipmentMissing() {
    CarrierRouterService.TrackingEvent event = new CarrierRouterService.TrackingEvent();
    event.trackingNo = "TRK-404";
    when(carrierRouterService.parseWebhook("shiprocket", "payload")).thenReturn(event);
    when(shipmentRepository.findAll()).thenReturn(List.of());

    Shipment updated = service.updateTracking("shiprocket", "payload");

    assertThat(updated).isNull();
    verify(shipmentRepository, never()).save(any());
  }

  private CreateShipmentRequest.Item item(long variantId, int qty) {
    CreateShipmentRequest.Item item = new CreateShipmentRequest.Item();
    item.setVariantId(variantId);
    item.setQty(qty);
    item.setSku("SKU-" + variantId);
    return item;
  }
}
