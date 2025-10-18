package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.RateQuoteRequest;
import com.maavooripachadi.shipping.dto.RateQuoteResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipping")
public class ShippingPublicController {
    private final RateService rates; private final TrackingEventRepository tracks; private final ShipmentRepository ships;
    public ShippingPublicController(RateService rates, TrackingEventRepository tracks, ShipmentRepository ships){ this.rates=rates; this.tracks=tracks; this.ships=ships; }

    @PostMapping("/quote")
    public List<RateQuoteResponse> quote(@RequestBody RateQuoteRequest req){ return rates.quote(req); }

    @GetMapping("/track/{orderNo}")
    public java.util.Map<String,Object> track(@PathVariable String orderNo){
        var s = ships.findByOrderNo(orderNo).orElse(null);
        var ev = s==null? java.util.List.of() : tracks.findByShipmentIdOrderByCreatedAtAsc(s.getId());
        return java.util.Map.of("shipment", s, "events", ev);
    }
}
