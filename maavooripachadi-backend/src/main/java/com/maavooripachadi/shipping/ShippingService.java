package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.CreateShipmentRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShippingService {
    private final ShipmentRepository shipments; private final TrackingEventRepository tracks; private final ShiprocketShippingClient shiprocket;
    public ShippingService(ShipmentRepository shipments, TrackingEventRepository tracks, ShiprocketShippingClient shiprocket){ this.shipments = shipments; this.tracks = tracks; this.shiprocket = shiprocket; }

    @Transactional
    public Shipment create(CreateShipmentRequest req){
        Shipment s = shipments.findByOrderNo(req.getOrderNo()).orElse(new Shipment());
        s.setOrderNo(req.getOrderNo()); s.setFromPincode(req.getFromPincode()); s.setToPincode(req.getToPincode()); s.setToName(req.getToName()); s.setToPhone(req.getToPhone()); s.setToAddress1(req.getToAddress1()); s.setToAddress2(req.getToAddress2()); s.setToCity(req.getToCity()); s.setToState(req.getToState()); s.setWeightGrams(req.getWeightGrams()); s.setLengthCm(req.getLengthCm()); s.setWidthCm(req.getWidthCm()); s.setHeightCm(req.getHeightCm()); s.setServiceLevel(req.getServiceLevel()); s.setCodPaise(req.getCodPaise()); s.setStatus(ShipmentStatus.RATE_REQUESTED);
        return shipments.save(s);
    }

    @Transactional
    public Shipment buyLabel(String orderNo){
        Shipment s = shipments.findByOrderNo(orderNo).orElseThrow();
        s.setLabelStatus(LabelStatus.REQUESTED);
        ShiprocketShippingClient.LabelResult r = shiprocket.createLabel(s);
        s.setTrackingNo(r.awb); s.setLabelUrl(r.labelUrl); s.setLabelStatus(LabelStatus.READY); s.setStatus(ShipmentStatus.LABEL_PURCHASED);
        return shipments.save(s);
    }

    @Transactional
    public Shipment markDispatched(String orderNo){
        Shipment s = shipments.findByOrderNo(orderNo).orElseThrow();
        s.setStatus(ShipmentStatus.DISPATCHED);
        return shipments.save(s);
    }

    @Transactional
    public void addTracking(String orderNo, String status, String location, String details){
        Shipment s = shipments.findByOrderNo(orderNo).orElseThrow();
        TrackingEvent te = new TrackingEvent(); te.setShipment(s); te.setStatus(status); te.setLocation(location); te.setDetails(details); tracks.save(te);
        if ("DELIVERED".equalsIgnoreCase(status)) { s.setStatus(ShipmentStatus.DELIVERED); shipments.save(s); }
    }

    @Transactional
    public Shipment cancel(String orderNo){
        Shipment s = shipments.findByOrderNo(orderNo).orElseThrow();
        if (s.getTrackingNo()!=null) shiprocket.cancel(s.getTrackingNo());
        s.setStatus(ShipmentStatus.CANCELLED); s.setLabelStatus(LabelStatus.CANCELLED);
        return shipments.save(s);
    }
}
