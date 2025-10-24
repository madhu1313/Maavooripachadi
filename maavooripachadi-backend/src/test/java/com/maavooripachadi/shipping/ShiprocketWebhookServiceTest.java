package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.ShiprocketWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ShiprocketWebhookServiceTest {

    private ShippingService shippingService;
    private ShiprocketWebhookService webhookService;

    @BeforeEach
    void setUp() {
        shippingService = mock(ShippingService.class);
        webhookService = new ShiprocketWebhookService(shippingService);
    }

    @Test
    void handleUsesCurrentStatusWhenAvailable() {
        ShiprocketWebhook webhook = new ShiprocketWebhook();
        webhook.setOrderId("MP-1001");
        webhook.setAwb("AWB123");
        webhook.setCurrentStatus("DELIVERED");
        webhook.setLocation("Hyderabad");
        webhook.setTrackUrl("https://track");

        webhookService.handle(webhook);

        verify(shippingService).addTracking("MP-1001", "DELIVERED", "Hyderabad", "awb=AWB123,url=https://track");
    }

    @Test
    void handleFallsBackToCurrentScanWhenStatusMissing() {
        ShiprocketWebhook webhook = new ShiprocketWebhook();
        webhook.setOrderId("MP-1002");
        webhook.setAwb("AWB456");
        webhook.setCurrentScan("OUT FOR DELIVERY");
        webhook.setLocation("Bengaluru");
        webhook.setTrackUrl("https://track2");

        webhookService.handle(webhook);

        verify(shippingService).addTracking("MP-1002", "OUT FOR DELIVERY", "Bengaluru", "awb=AWB456,url=https://track2");
    }
}
