package com.maavooripachadi.logistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CarrierRouterServiceTest {

  private CarrierAccountRepository carrierAccountRepository;
  private ShiprocketClient shiprocketClient;
  private CarrierRouterService service;

  @BeforeEach
  void setUp() {
    carrierAccountRepository = mock(CarrierAccountRepository.class);
    shiprocketClient = mock(ShiprocketClient.class);
    service = new CarrierRouterService(carrierAccountRepository, shiprocketClient);
  }

  @Test
  void buyLabelDefaultsToShiprocketWhenCarrierMissing() {
    CarrierAccount account = new CarrierAccount();
    CarrierRouterService.LabelResult result = new CarrierRouterService.LabelResult();
    result.labelUrl = "https://labels/sample.pdf";
    result.trackingNo = "SR123";

    when(carrierAccountRepository.findFirstByCarrierAndEnabledTrue("SHIPROCKET"))
        .thenReturn(Optional.of(account));
    when(shiprocketClient.createLabel(eq(account), any(Shipment.class))).thenReturn(result);

    Shipment shipment = new Shipment();
    CarrierRouterService.LabelResult response = service.buyLabel(shipment);

    assertThat(response.labelUrl).isEqualTo("https://labels/sample.pdf");
    assertThat(response.trackingNo).isEqualTo("SR123");
    verify(shiprocketClient).createLabel(account, shipment);
  }

  @Test
  void buyLabelThrowsWhenCarrierUnsupported() {
    Shipment shipment = new Shipment();
    shipment.setCarrier("DHL");

    assertThatThrownBy(() -> service.buyLabel(shipment))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("Carrier not configured");
  }

  @Test
  void parseWebhookDelegatesToShiprocketClient() {
    CarrierRouterService.TrackingEvent event = new CarrierRouterService.TrackingEvent();
    event.trackingNo = "SR999";
    event.status = "IN_TRANSIT";
    when(shiprocketClient.parseTracking("payload")).thenReturn(event);

    CarrierRouterService.TrackingEvent parsed = service.parseWebhook("shiprocket", "payload");

    assertThat(parsed.trackingNo).isEqualTo("SR999");
    verify(shiprocketClient).parseTracking("payload");
  }

  @Test
  void parseWebhookThrowsForUnsupportedCarrier() {
    assertThatThrownBy(() -> service.parseWebhook("dhl", "{}"))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessageContaining("Carrier webhook not supported");
  }
}
