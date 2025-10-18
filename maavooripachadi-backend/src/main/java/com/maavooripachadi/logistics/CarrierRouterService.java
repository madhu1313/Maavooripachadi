package com.maavooripachadi.logistics;


import org.springframework.stereotype.Service;


@Service
public class CarrierRouterService {
    private final CarrierAccountRepository carrierRepo;
    private final ShiprocketClient shiprocketClient;


    public CarrierRouterService(CarrierAccountRepository carrierRepo, ShiprocketClient shiprocketClient){
        this.carrierRepo = carrierRepo; this.shiprocketClient = shiprocketClient;
    }


    public LabelResult buyLabel(Shipment shipment){
        String carrier = shipment.getCarrier() != null ? shipment.getCarrier() : "SHIPROCKET";
        if ("SHIPROCKET".equalsIgnoreCase(carrier)){
            var acc = carrierRepo.findFirstByCarrierAndEnabledTrue("SHIPROCKET").orElse(null);
            return shiprocketClient.createLabel(acc, shipment);
        }
        throw new UnsupportedOperationException("Carrier not configured: " + carrier);
    }


    public TrackingEvent parseWebhook(String carrier, String payload){
        if ("SHIPROCKET".equalsIgnoreCase(carrier)){
            return shiprocketClient.parseTracking(payload);
        }
        throw new UnsupportedOperationException("Carrier webhook not supported: " + carrier);
    }


    // results
    public static class LabelResult { public String labelUrl; public String trackingNo; }
    public static class TrackingEvent { public String trackingNo; public String status; public String description; }
}