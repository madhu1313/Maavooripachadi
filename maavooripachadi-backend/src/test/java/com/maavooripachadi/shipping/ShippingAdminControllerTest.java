package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.CreateShipmentRequest;
import com.maavooripachadi.shipping.dto.ShiprocketWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ShippingAdminControllerTest {

    private ShippingService shippingService;
    private ShippingZoneRepository zoneRepository;
    private RateCardRepository rateCardRepository;
    private ShiprocketWebhookService webhookService;
    private ShippingAdminController controller;

    @BeforeEach
    void setUp() {
        shippingService = mock(ShippingService.class);
        zoneRepository = mock(ShippingZoneRepository.class);
        rateCardRepository = mock(RateCardRepository.class);
        webhookService = mock(ShiprocketWebhookService.class);
        controller = new ShippingAdminController(shippingService, zoneRepository, rateCardRepository, webhookService);
    }

    @Test
    void createDelegatesToShippingService() {
        CreateShipmentRequest request = new CreateShipmentRequest();
        Shipment shipment = new Shipment();
        when(shippingService.create(request)).thenReturn(shipment);

        Shipment result = controller.create(request);

        assertThat(result).isSameAs(shipment);
        verify(shippingService).create(request);
    }

    @Test
    void buyDelegatesToService() {
        Shipment shipment = new Shipment();
        when(shippingService.buyLabel("MP-1001")).thenReturn(shipment);

        Shipment result = controller.buy("MP-1001");

        assertThat(result).isSameAs(shipment);
        verify(shippingService).buyLabel("MP-1001");
    }

    @Test
    void dispatchDelegatesToService() {
        Shipment shipment = new Shipment();
        when(shippingService.markDispatched("MP-1002")).thenReturn(shipment);

        Shipment result = controller.dispatch("MP-1002");

        assertThat(result).isSameAs(shipment);
        verify(shippingService).markDispatched("MP-1002");
    }

    @Test
    void cancelDelegatesToService() {
        Shipment shipment = new Shipment();
        when(shippingService.cancel("MP-1003")).thenReturn(shipment);

        Shipment result = controller.cancel("MP-1003");

        assertThat(result).isSameAs(shipment);
        verify(shippingService).cancel("MP-1003");
    }

    @Test
    void saveZonePersistsEntity() {
        ShippingZone zone = new ShippingZone();
        when(zoneRepository.save(zone)).thenReturn(zone);

        ShippingZone result = controller.saveZone(zone);

        assertThat(result).isSameAs(zone);
        verify(zoneRepository).save(zone);
    }

    @Test
    void saveRatePersistsRateCard() {
        RateCard rateCard = new RateCard();
        when(rateCardRepository.save(rateCard)).thenReturn(rateCard);

        RateCard result = controller.saveRate(rateCard);

        assertThat(result).isSameAs(rateCard);
        verify(rateCardRepository).save(rateCard);
    }

    @Test
    void shiprocketWebhookDelegatesToService() {
        ShiprocketWebhook webhook = new ShiprocketWebhook();

        controller.shiprocket(webhook);

        verify(webhookService).handle(webhook);
    }
}
