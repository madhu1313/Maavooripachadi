package com.maavooripachadi.logistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AllocationServiceTest {

  private WarehouseRepository warehouseRepository;
  private WarehouseStockRepository stockRepository;
  private AllocationService service;
  private Warehouse warehouse;

  @BeforeEach
  void setUp() {
    warehouseRepository = mock(WarehouseRepository.class);
    stockRepository = mock(WarehouseStockRepository.class);
    service = new AllocationService(warehouseRepository, stockRepository);

    warehouse = new Warehouse();
    warehouse.setCode("HYD");
    ReflectionTestUtils.setField(warehouse, "id", 1L);
  }

  @Test
  void chooseReturnsFirstActiveWarehouse() {
    Warehouse another = new Warehouse();
    when(warehouseRepository.findByActiveTrue()).thenReturn(List.of(warehouse, another));

    Warehouse chosen = service.choose("500001");

    assertThat(chosen).isSameAs(warehouse);
    verify(warehouseRepository).findByActiveTrue();
  }

  @Test
  void reserveIncrementsReservedWhenStockAvailable() {
    WarehouseStock stock = new WarehouseStock();
    stock.setWarehouse(warehouse);
    stock.setVariantId(99L);
    stock.setOnHand(10);
    stock.setReserved(2);

    when(stockRepository.findByWarehouse(warehouse)).thenReturn(List.of(stock));
    when(stockRepository.save(any(WarehouseStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.reserve(99L, 3, warehouse);

    assertThat(stock.getReserved()).isEqualTo(5);
    verify(stockRepository).save(stock);
  }

  @Test
  void reserveThrowsWhenInsufficientInventory() {
    WarehouseStock stock = new WarehouseStock();
    stock.setWarehouse(warehouse);
    stock.setVariantId(15L);
    stock.setOnHand(4);
    stock.setReserved(3);

    when(stockRepository.findByWarehouse(warehouse)).thenReturn(List.of(stock));

    assertThatThrownBy(() -> service.reserve(15L, 2, warehouse))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("insufficient");

    verify(stockRepository, never()).save(stock);
  }

  @Test
  void convertMovesUnitsFromReservedToOnHand() {
    WarehouseStock stock = new WarehouseStock();
    stock.setWarehouse(warehouse);
    stock.setVariantId(31L);
    stock.setOnHand(10);
    stock.setReserved(6);

    when(stockRepository.findByWarehouse(warehouse)).thenReturn(List.of(stock));

    service.convert(31L, 4, warehouse);

    assertThat(stock.getReserved()).isEqualTo(2);
    assertThat(stock.getOnHand()).isEqualTo(6);
    verify(stockRepository).save(stock);
  }

  @Test
  void releaseDoesNotAllowNegativeReserved() {
    WarehouseStock stock = new WarehouseStock();
    stock.setWarehouse(warehouse);
    stock.setVariantId(77L);
    stock.setReserved(3);

    when(stockRepository.findByWarehouse(warehouse)).thenReturn(List.of(stock));

    service.release(77L, 5, warehouse);

    assertThat(stock.getReserved()).isZero();
    verify(stockRepository).save(stock);
  }
}
