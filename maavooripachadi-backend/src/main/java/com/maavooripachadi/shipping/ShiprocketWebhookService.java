package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.ShiprocketWebhook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShiprocketWebhookService {
    private final ShippingService svc;
    public ShiprocketWebhookService(ShippingService svc){ this.svc = svc; }

    @Transactional
    public void handle(ShiprocketWebhook w){
        String status = w.getCurrentStatus()==null? w.getCurrentScan() : w.getCurrentStatus();
        svc.addTracking(w.getOrderId(), status, w.getLocation(), "awb="+w.getAwb()+",url="+w.getTrackUrl());
    }
}
