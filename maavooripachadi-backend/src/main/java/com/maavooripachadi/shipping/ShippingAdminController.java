package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.CreateShipmentRequest;
import com.maavooripachadi.shipping.dto.ShiprocketWebhook;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/shipping")
public class ShippingAdminController {
    private final ShippingService service; private final ShippingZoneRepository zones; private final RateCardRepository rateCards; private final ShiprocketWebhookService webhookSvc;
    public ShippingAdminController(ShippingService service, ShippingZoneRepository zones, RateCardRepository rateCards, ShiprocketWebhookService webhookSvc){ this.service=service; this.zones=zones; this.rateCards=rateCards; this.webhookSvc=webhookSvc; }

    @PostMapping("/shipment")
    @PreAuthorize("hasAuthority('FULFILLMENT_WRITE') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Shipment create(@RequestBody CreateShipmentRequest req){ return service.create(req); }

    @PostMapping("/shipment/{orderNo}/buy")
    @PreAuthorize("hasAuthority('FULFILLMENT_WRITE') or hasRole('ADMIN')")
    public Shipment buy(@PathVariable String orderNo){ return service.buyLabel(orderNo); }

    @PostMapping("/shipment/{orderNo}/dispatch")
    @PreAuthorize("hasAuthority('FULFILLMENT_WRITE') or hasRole('ADMIN')")
    public Shipment dispatch(@PathVariable String orderNo){ return service.markDispatched(orderNo); }

    @PostMapping("/shipment/{orderNo}/cancel")
    @PreAuthorize("hasAuthority('FULFILLMENT_WRITE') or hasRole('ADMIN')")
    public Shipment cancel(@PathVariable String orderNo){ return service.cancel(orderNo); }

    @PostMapping("/zone")
    @PreAuthorize("hasAuthority('FULFILLMENT_WRITE') or hasRole('ADMIN')")
    public ShippingZone saveZone(@RequestBody ShippingZone z){ return zones.save(z); }

    @PostMapping("/rate")
    @PreAuthorize("hasAuthority('FULFILLMENT_WRITE') or hasRole('ADMIN')")
    public RateCard saveRate(@RequestBody RateCard r){ return rateCards.save(r); }

    // Shiprocket webhook (tracking update)
    @PostMapping("/shiprocket/webhook")
    public void shiprocket(@RequestBody ShiprocketWebhook body){ webhookSvc.handle(body); }
}
