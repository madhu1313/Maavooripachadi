package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.RateQuoteRequest;
import com.maavooripachadi.shipping.dto.RateQuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ShippingPublicControllerTest {

    private RateService rateService;
    private TrackingEventRepository trackingEventRepository;
    private ShipmentRepository shipmentRepository;
    private ShippingPublicController controller;

    @BeforeEach
    void setUp() {
        rateService = mock(RateService.class);
        trackingEventRepository = mock(TrackingEventRepository.class);
        shipmentRepository = mock(ShipmentRepository.class);
        controller = new ShippingPublicController(rateService, trackingEventRepository, shipmentRepository);
    }

    @Test
    void quoteDelegatesToRateService() {
        RateQuoteRequest request = new RateQuoteRequest();
        RateQuoteResponse response = new RateQuoteResponse();
        response.setAmountPaise(1500);
        when(rateService.quote(request)).thenReturn(List.of(response));

        List<RateQuoteResponse> responses = controller.quote(request);

        assertThat(responses).containsExactly(response);
        verify(rateService).quote(request);
    }

    @Test
    void trackReturnsShipmentWithEventsWhenAvailable() {
        Shipment shipment = new Shipment();
        shipment.setOrderNo("MP-1001");
        ReflectionTestUtils.setField(shipment, "id", 42L);
        TrackingEvent event = new TrackingEvent();
        event.setShipment(shipment);
        event.setStatus("IN TRANSIT");

        when(shipmentRepository.findByOrderNo("MP-1001")).thenReturn(Optional.of(shipment));
        when(trackingEventRepository.findByShipmentIdOrderByCreatedAtAsc(42L)).thenReturn(List.of(event));

        Map<String, Object> payload = controller.track("MP-1001");

        assertThat(payload.get("shipment")).isSameAs(shipment);
        assertThat(payload.get("events")).asList().containsExactly(event);
    }

    @Test
    void trackReturnsEmptyPayloadWhenShipmentMissing() {
        when(shipmentRepository.findByOrderNo("MISSING")).thenReturn(Optional.empty());

        Map<String, Object> payload = controller.track("MISSING");

        assertThat(payload.get("shipment")).isNull();
        assertThat(payload.get("events")).asList().isEmpty();
        verify(trackingEventRepository, never()).findByShipmentIdOrderByCreatedAtAsc(anyLong());
    }
}
