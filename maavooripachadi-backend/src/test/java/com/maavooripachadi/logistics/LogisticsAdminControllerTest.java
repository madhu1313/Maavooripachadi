package com.maavooripachadi.logistics;

import com.maavooripachadi.logistics.dto.AdjustInventoryRequest;
import com.maavooripachadi.logistics.dto.CreateShipmentRequest;
import com.maavooripachadi.logistics.dto.LabelRequest;
import com.maavooripachadi.logistics.dto.ReserveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class LogisticsAdminControllerTest {

  private InventoryService inventoryService;
  private ShipmentService shipmentService;
  private WarehouseRepository warehouseRepository;
  private LogisticsAdminController controller;

  @BeforeEach
  void setUp() {
    inventoryService = mock(InventoryService.class);
    shipmentService = mock(ShipmentService.class);
    warehouseRepository = mock(WarehouseRepository.class);
    controller = new LogisticsAdminController(inventoryService, shipmentService, warehouseRepository);
  }

  @Test
  void upsertWarehouseCreatesNewRecordWhenMissing() {
    Warehouse request = new Warehouse();
    request.setCode("HYD");
    request.setName("Hyderabad FC");

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.empty());
    when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Warehouse saved = controller.upsertWarehouse(request);

    assertThat(saved.getName()).isEqualTo("Hyderabad FC");
    verify(warehouseRepository).save(argThat(warehouse ->
        "HYD".equals(warehouse.getCode()) && "Hyderabad FC".equals(warehouse.getName())
    ));
  }

  @Test
  void upsertWarehouseUpdatesExistingRecord() {
    Warehouse existing = new Warehouse();
    existing.setCode("HYD");
    existing.setName("Old Name");

    Warehouse request = new Warehouse();
    request.setCode("HYD");
    request.setName("New Name");
    request.setCity("Hyderabad");

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(existing));
    when(warehouseRepository.save(existing)).thenReturn(existing);

    Warehouse saved = controller.upsertWarehouse(request);

    assertThat(saved.getName()).isEqualTo("New Name");
    assertThat(saved.getCity()).isEqualTo("Hyderabad");
    verify(warehouseRepository).save(existing);
  }

  @Test
  void upsertWarehouseRejectsMissingCode() {
    Warehouse request = new Warehouse();
    request.setName("No Code");

    assertThatThrownBy(() -> controller.upsertWarehouse(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("code required");
  }

  @Test
  void listWarehousesReturnsRepositoryEntries() {
    Warehouse warehouse = new Warehouse();
    when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

    assertThat(controller.listWarehouses()).containsExactly(warehouse);
    verify(warehouseRepository).findAll();
  }

  @Test
  void adjustInventoryDelegatesToService() {
    AdjustInventoryRequest request = new AdjustInventoryRequest();
    request.setWarehouseCode("HYD");
    request.setVariantId(11L);
    request.setDeltaOnHand(2);
    request.setReason("count");

    Inventory inventory = new Inventory();
    inventory.setOnHand(5);

    when(inventoryService.adjust(request)).thenReturn(inventory);

    Inventory response = controller.adjust(request);

    assertThat(response).isSameAs(inventory);
    verify(inventoryService).adjust(request);
  }

  @Test
  void reserveInventoryWrapsResponse() {
    ReserveRequest request = new ReserveRequest();
    request.setWarehouseCode("HYD");
    request.setVariantId(22L);
    request.setQty(3);

    Map<String, Object> response = controller.reserve(request);

    assertThat(response).containsEntry("ok", true);
    verify(inventoryService).reserve(request);
  }

  @Test
  void unreserveInventoryDelegatesToService() {
    Map<String, Object> response = controller.unreserve("HYD", 33L, 2, "cancel");

    assertThat(response).containsEntry("ok", true);
    verify(inventoryService).unreserve("HYD", 33L, 2, "cancel");
  }

  @Test
  void createShipmentDelegatesToService() {
    CreateShipmentRequest request = new CreateShipmentRequest();
    Shipment shipment = new Shipment();

    when(shipmentService.create(request)).thenReturn(shipment);

    Shipment created = controller.createShipment(request);

    assertThat(created).isSameAs(shipment);
    verify(shipmentService).create(request);
  }

  @Test
  void labelShipmentDelegatesToService() {
    LabelRequest request = new LabelRequest();
    request.setShipmentNo("SHP-1");
    Shipment shipment = new Shipment();

    when(shipmentService.buyLabel(request)).thenReturn(shipment);

    Shipment labeled = controller.label(request);

    assertThat(labeled).isSameAs(shipment);
    verify(shipmentService).buyLabel(request);
  }

  @Test
  void listShipmentsReturnsPagedData() {
    Page<Shipment> page = new PageImpl<>(List.of(new Shipment()));
    when(shipmentService.listByStatus(ShipmentStatus.IN_TRANSIT, 0, 20)).thenReturn(page);

    Page<Shipment> response = controller.list(ShipmentStatus.IN_TRANSIT, 0, 20);

    assertThat(response).isSameAs(page);
    verify(shipmentService).listByStatus(ShipmentStatus.IN_TRANSIT, 0, 20);
  }
}
