package com.maavooripachadi.logistics;

import com.maavooripachadi.logistics.CarrierRouterService.LabelResult;
import com.maavooripachadi.logistics.CarrierRouterService.TrackingEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShiprocketClientTest {

    private final ShiprocketClient client = new ShiprocketClient();

    @Test
    void createLabelGeneratesTrackingAndLabelUrl() {
        CarrierAccount account = new CarrierAccount();
        account.setCarrier("SHIPROCKET");
        Shipment shipment = new Shipment();
        shipment.setShipmentNo("SN-1");

        LabelResult result = client.createLabel(account, shipment);

        assertNotNull(result);
        assertNotNull(result.trackingNo);
        assertTrue(result.trackingNo.startsWith("SR"));
        assertNotNull(result.labelUrl);
        assertTrue(result.labelUrl.contains(result.trackingNo));
    }

    @Test
    void parseTrackingMapsPayloadIntoTrackingEvent() {
        String payload = "{\"event\":\"ARRIVED\"}";

        TrackingEvent event = client.parseTracking(payload);

        assertEquals("UNKNOWN", event.trackingNo);
        assertEquals("IN_TRANSIT", event.status);
        assertEquals(payload, event.description);
    }
}
