package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.CreateShipmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShippingServiceTest {

    private ShipmentRepository shipmentRepository;
    private TrackingEventRepository trackingEventRepository;
    private ShiprocketShippingClient shiprocketClient;
    private ShippingService service;

    @BeforeEach
    void setUp() {
        shipmentRepository = mock(ShipmentRepository.class);
        trackingEventRepository = mock(TrackingEventRepository.class);
        shiprocketClient = mock(ShiprocketShippingClient.class);
        service = new ShippingService(shipmentRepository, trackingEventRepository, shiprocketClient);
    }

    @Test
    void createPersistsNewShipmentWithRequestDetails() {
        CreateShipmentRequest request = new CreateShipmentRequest();
        request.setOrderNo("MP-1001");
        request.setFromPincode("500032");
        request.setToPincode("560001");
        request.setToName("Anand");
        request.setToPhone("9999999999");
        request.setToAddress1("Street 1");
        request.setToAddress2("Street 2");
        request.setToCity("Bengaluru");
        request.setToState("KA");
        request.setWeightGrams(1200);
        request.setLengthCm(10);
        request.setWidthCm(8);
        request.setHeightCm(6);
        request.setServiceLevel(ServiceLevel.EXPRESS);
        request.setCodPaise(10000);

        when(shipmentRepository.findByOrderNo("MP-1001")).thenReturn(Optional.empty());
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shipment created = service.create(request);

        assertThat(created.getOrderNo()).isEqualTo("MP-1001");
        assertThat(created.getToCity()).isEqualTo("Bengaluru");
        assertThat(created.getServiceLevel()).isEqualTo(ServiceLevel.EXPRESS);
        assertThat(created.getStatus()).isEqualTo(ShipmentStatus.RATE_REQUESTED);
        verify(shipmentRepository).save(created);
    }

    @Test
    void buyLabelInvokesShiprocketAndUpdatesShipment() {
        Shipment shipment = new Shipment();
        shipment.setOrderNo("MP-1002");
        when(shipmentRepository.findByOrderNo("MP-1002")).thenReturn(Optional.of(shipment));

        ShiprocketShippingClient.LabelResult labelResult = new ShiprocketShippingClient.LabelResult();
        labelResult.awb = "AWB123";
        labelResult.labelUrl = "https://label";
        when(shiprocketClient.createLabel(shipment)).thenReturn(labelResult);
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shipment updated = service.buyLabel("MP-1002");

        assertThat(updated.getTrackingNo()).isEqualTo("AWB123");
        assertThat(updated.getLabelStatus()).isEqualTo(LabelStatus.READY);
        assertThat(updated.getStatus()).isEqualTo(ShipmentStatus.LABEL_PURCHASED);
        verify(shiprocketClient).createLabel(shipment);
    }

    @Test
    void markDispatchedUpdatesStatus() {
        Shipment shipment = new Shipment();
        shipment.setOrderNo("MP-1003");
        when(shipmentRepository.findByOrderNo("MP-1003")).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shipment updated = service.markDispatched("MP-1003");

        assertThat(updated.getStatus()).isEqualTo(ShipmentStatus.DISPATCHED);
        verify(shipmentRepository).save(shipment);
    }

    @Test
    void addTrackingPersistsEventAndMarksDeliveredWhenStatusMatches() {
        Shipment shipment = new Shipment();
        shipment.setOrderNo("MP-1004");
        when(shipmentRepository.findByOrderNo("MP-1004")).thenReturn(Optional.of(shipment));

        TrackingEvent savedEvent = new TrackingEvent();
        when(trackingEventRepository.save(any(TrackingEvent.class))).thenReturn(savedEvent);

        service.addTracking("MP-1004", "DELIVERED", "Hyderabad", "Delivered");

        ArgumentCaptor<TrackingEvent> eventCaptor = ArgumentCaptor.forClass(TrackingEvent.class);
        verify(trackingEventRepository).save(eventCaptor.capture());
        TrackingEvent captured = eventCaptor.getValue();
        assertThat(captured.getShipment()).isSameAs(shipment);
        assertThat(captured.getStatus()).isEqualTo("DELIVERED");
        assertThat(captured.getLocation()).isEqualTo("Hyderabad");
        assertThat(captured.getDetails()).contains("Delivered");

        verify(shipmentRepository).save(shipment);
        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
    }

    @Test
    void addTrackingDoesNotUpdateShipmentWhenNotDelivered() {
        Shipment shipment = new Shipment();
        shipment.setOrderNo("MP-1005");
        when(shipmentRepository.findByOrderNo("MP-1005")).thenReturn(Optional.of(shipment));

        service.addTracking("MP-1005", "IN_TRANSIT", "Hub", "Moved to hub");

        verify(trackingEventRepository).save(any(TrackingEvent.class));
        verify(shipmentRepository, never()).save(shipment);
        assertThat(shipment.getStatus()).isNotEqualTo(ShipmentStatus.DELIVERED);
    }

    @Test
    void cancelCallsShiprocketWhenTrackingExistsAndUpdatesStatus() {
        Shipment shipment = new Shipment();
        shipment.setOrderNo("MP-1006");
        shipment.setTrackingNo("AWB987");
        when(shipmentRepository.findByOrderNo("MP-1006")).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shipment cancelled = service.cancel("MP-1006");

        verify(shiprocketClient).cancel("AWB987");
        assertThat(cancelled.getStatus()).isEqualTo(ShipmentStatus.CANCELLED);
        assertThat(cancelled.getLabelStatus()).isEqualTo(LabelStatus.CANCELLED);
    }
}
