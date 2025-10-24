package com.maavooripachadi.logistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LogisticsWebhookControllerTest {

  private ShipmentService shipmentService;
  private LogisticsWebhookController controller;

  @BeforeEach
  void setUp() {
    shipmentService = mock(ShipmentService.class);
    controller = new LogisticsWebhookController(shipmentService);
  }

  @Test
  void trackingEndpointReturnsUpdatedShipmentNumber() {
    Shipment shipment = new Shipment();
    shipment.setShipmentNo("SHP-123");

    when(shipmentService.updateTracking(eq("shiprocket"), eq("{payload}"))).thenReturn(shipment);

    Map<String, Object> response = controller.tracking("shiprocket", "{payload}");

    assertThat(response).containsEntry("ok", true).containsEntry("updated", "SHP-123");
    verify(shipmentService).updateTracking("shiprocket", "{payload}");
  }

  @Test
  void trackingEndpointHandlesMissingShipment() {
    when(shipmentService.updateTracking("shiprocket", "{payload}")).thenReturn(null);

    assertThatThrownBy(() -> controller.tracking("shiprocket", "{payload}"))
        .isInstanceOf(NullPointerException.class);
  }
}
