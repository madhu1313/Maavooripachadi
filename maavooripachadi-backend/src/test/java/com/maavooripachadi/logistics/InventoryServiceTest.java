package com.maavooripachadi.logistics;

import com.maavooripachadi.logistics.dto.AdjustInventoryRequest;
import com.maavooripachadi.logistics.dto.ReserveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

  private InventoryRepository inventoryRepository;
  private WarehouseRepository warehouseRepository;
  private StockMovementRepository movementRepository;
  private InventoryService service;
  private Warehouse warehouse;

  @BeforeEach
  void setUp() {
    inventoryRepository = mock(InventoryRepository.class);
    warehouseRepository = mock(WarehouseRepository.class);
    movementRepository = mock(StockMovementRepository.class);
    service = new InventoryService(inventoryRepository, warehouseRepository, movementRepository);

    warehouse = new Warehouse();
    warehouse.setCode("HYD");
    ReflectionTestUtils.setField(warehouse, "id", 44L);
  }

  @Test
  void adjustCreatesInventoryRowWhenMissing() {
    AdjustInventoryRequest request = new AdjustInventoryRequest();
    request.setWarehouseCode("HYD");
    request.setVariantId(9001L);
    request.setDeltaOnHand(5);
    request.setReason("cycle");

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(warehouse));
    when(inventoryRepository.findByWarehouseIdAndVariantId(44L, 9001L)).thenReturn(Optional.empty());
    when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Inventory adjusted = service.adjust(request);

    assertThat(adjusted.getOnHand()).isEqualTo(5);
    verify(inventoryRepository, times(2)).save(any(Inventory.class)); // one for create, one after update
    verify(movementRepository).save(argThat(movement ->
        movement.getType() == MovementType.ADJUST &&
            movement.getQuantity() == 5 &&
            "cycle".equals(movement.getReason())
    ));
  }

  @Test
  void adjustThrowsWhenDeltaWouldGoNegative() {
    Inventory inventory = new Inventory();
    inventory.setOnHand(1);
    inventory.setReserved(0);

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(warehouse));
    when(inventoryRepository.findByWarehouseIdAndVariantId(44L, 9002L)).thenReturn(Optional.of(inventory));

    AdjustInventoryRequest request = new AdjustInventoryRequest();
    request.setWarehouseCode("HYD");
    request.setVariantId(9002L);
    request.setDeltaOnHand(-3);
    request.setReason("audit");

    assertThatThrownBy(() -> service.adjust(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Insufficient");

    verify(movementRepository, never()).save(any());
  }

  @Test
  void reserveIncrementsReservedAndAddsMovement() {
    Inventory inventory = new Inventory();
    inventory.setOnHand(10);
    inventory.setReserved(2);

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(warehouse));
    when(inventoryRepository.findByWarehouseIdAndVariantId(44L, 55L)).thenReturn(Optional.of(inventory));
    when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ReserveRequest request = new ReserveRequest();
    request.setWarehouseCode("HYD");
    request.setVariantId(55L);
    request.setQty(4);
    request.setOrderNo("ORD-1");

    service.reserve(request);

    assertThat(inventory.getReserved()).isEqualTo(6);
    verify(movementRepository).save(argThat(movement ->
        movement.getType() == MovementType.RESERVE &&
            movement.getQuantity() == 4 &&
            "ORD-1".equals(movement.getReason())
    ));
  }

  @Test
  void reserveFailsWhenNotEnoughAvailable() {
    Inventory inventory = new Inventory();
    inventory.setOnHand(3);
    inventory.setReserved(3);

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(warehouse));
    when(inventoryRepository.findByWarehouseIdAndVariantId(44L, 77L)).thenReturn(Optional.of(inventory));

    ReserveRequest request = new ReserveRequest();
    request.setWarehouseCode("HYD");
    request.setVariantId(77L);
    request.setQty(1);

    assertThatThrownBy(() -> service.reserve(request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Not enough stock");
  }

  @Test
  void unreserveReducesReservedButNotBelowZero() {
    Inventory inventory = new Inventory();
    inventory.setOnHand(5);
    inventory.setReserved(2);

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(warehouse));
    when(inventoryRepository.findByWarehouseIdAndVariantId(44L, 88L)).thenReturn(Optional.of(inventory));
    when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.unreserve("HYD", 88L, 5, "cancelled");

    assertThat(inventory.getReserved()).isZero();
    verify(movementRepository).save(argThat(movement ->
        movement.getType() == MovementType.UNRESERVE &&
            movement.getQuantity() == 5 &&
            "cancelled".equals(movement.getReason())
    ));
  }

  @Test
  void allocateAndShipConsumesReservedStockAndAddsMovements() {
    Inventory inventory = new Inventory();
    inventory.setOnHand(12);
    inventory.setReserved(6);

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(warehouse));
    when(inventoryRepository.findByWarehouseIdAndVariantId(44L, 33L)).thenReturn(Optional.of(inventory));
    when(inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.allocateAndShip("HYD", 33L, 4, "ORD-99");

    assertThat(inventory.getReserved()).isEqualTo(2);
    assertThat(inventory.getOnHand()).isEqualTo(8);
    verify(movementRepository, times(2)).save(any(StockMovement.class));
    verify(movementRepository).save(argThat(movement ->
        movement.getType() == MovementType.ALLOCATE && movement.getQuantity() == 4
    ));
    verify(movementRepository).save(argThat(movement ->
        movement.getType() == MovementType.SHIP && movement.getQuantity() == 4
    ));
  }

  @Test
  void allocateAndShipFailsWhenReservedInsufficient() {
    Inventory inventory = new Inventory();
    inventory.setOnHand(10);
    inventory.setReserved(1);

    when(warehouseRepository.findByCode("HYD")).thenReturn(Optional.of(warehouse));
    when(inventoryRepository.findByWarehouseIdAndVariantId(44L, 40L)).thenReturn(Optional.of(inventory));

    assertThatThrownBy(() -> service.allocateAndShip("HYD", 40L, 2, "ORD-10"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Not reserved");

    verify(movementRepository, never()).save(any());
  }
}
