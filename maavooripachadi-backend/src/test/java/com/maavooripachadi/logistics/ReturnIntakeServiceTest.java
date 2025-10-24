package com.maavooripachadi.logistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReturnIntakeServiceTest {

  private WarehouseRepository warehouseRepository;
  private WarehouseStockRepository stockRepository;
  private ReturnIntakeRepository intakeRepository;
  private ReturnIntakeService service;
  private Warehouse warehouse;

  @BeforeEach
  void setUp() {
    warehouseRepository = mock(WarehouseRepository.class);
    stockRepository = mock(WarehouseStockRepository.class);
    intakeRepository = mock(ReturnIntakeRepository.class);
    service = new ReturnIntakeService(warehouseRepository, stockRepository, intakeRepository);

    warehouse = new Warehouse();
    ReflectionTestUtils.setField(warehouse, "id", 7L);
  }

  @Test
  void processRecordsIntakeAndRestocksInventory() {
    ReturnIntake intakeEntity = new ReturnIntake();
    when(intakeRepository.save(any(ReturnIntake.class))).thenAnswer(invocation -> {
      ReturnIntake saved = invocation.getArgument(0);
      ReflectionTestUtils.setField(saved, "id", 100L);
      return saved;
    });

    WarehouseStock stock = new WarehouseStock();
    stock.setWarehouse(warehouse);
    stock.setVariantId(55L);
    stock.setOnHand(3);

    when(warehouseRepository.findById(7L)).thenReturn(Optional.of(warehouse));
    when(stockRepository.findByWarehouse(warehouse)).thenReturn(List.of(stock));
    when(stockRepository.save(any(WarehouseStock.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.process(200L, 7L, "sealed", true, 55L, 2, "tester");

    ArgumentCaptor<ReturnIntake> intakeCaptor = ArgumentCaptor.forClass(ReturnIntake.class);
    verify(intakeRepository).save(intakeCaptor.capture());
    ReturnIntake savedIntake = intakeCaptor.getValue();
    assertThat(savedIntake.getReturnId()).isEqualTo(200L);
    assertThat(savedIntake.getProcessedBy()).isEqualTo("tester");
    assertThat(stock.getOnHand()).isEqualTo(5);
    verify(stockRepository).save(stock);
  }

  @Test
  void processSkipsRestockWhenFlagFalse() {
    service.process(201L, 7L, "damaged", false, 77L, 1, "tester");

    verify(intakeRepository).save(any(ReturnIntake.class));
    verifyNoInteractions(warehouseRepository);
    verify(stockRepository, never()).save(any());
  }
}
