package com.maavooripachadi.logistics;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/webhooks/logistics")
public class LogisticsWebhookController {
    private final ShipmentService shipments;


    public LogisticsWebhookController(ShipmentService shipments){ this.shipments = shipments; }


    @PostMapping("/{carrier}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public java.util.Map<String,Object> tracking(@PathVariable String carrier, @RequestBody String payload){
        var s = shipments.updateTracking(carrier, payload);
        return java.util.Map.of("ok", true, "updated", s != null ? s.getShipmentNo() : null);
    }
}