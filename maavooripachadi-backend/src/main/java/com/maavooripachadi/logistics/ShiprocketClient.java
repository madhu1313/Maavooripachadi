package com.maavooripachadi.logistics;


import org.springframework.stereotype.Component;


@Component
public class ShiprocketClient {
    public CarrierRouterService.LabelResult createLabel(CarrierAccount acc, Shipment s){
// TODO: call Shiprocket order/label API. Stub below.
        CarrierRouterService.LabelResult r = new CarrierRouterService.LabelResult();
        r.trackingNo = "SR" + System.currentTimeMillis();
        r.labelUrl = "https://labels.example/" + r.trackingNo + ".pdf";
        return r;
    }


    public CarrierRouterService.TrackingEvent parseTracking(String payload){
// TODO: map Shiprocket webhook JSON → TrackingEvent
        CarrierRouterService.TrackingEvent t = new CarrierRouterService.TrackingEvent();
        t.trackingNo = "UNKNOWN";
        t.status = "IN_TRANSIT";
        t.description = payload;
        return t;
    }
}