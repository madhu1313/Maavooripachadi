package com.maavooripachadi.pricing;


import com.maavooripachadi.pricing.dto.PriceQuoteRequest;
import com.maavooripachadi.pricing.dto.PriceQuoteResponse;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/pricing")
@Validated
public class PricingController {
    private final PricingService pricing;
    public PricingController(PricingService pricing){ this.pricing = pricing; }


    @PostMapping("/quote")
    public PriceQuoteResponse quote(@Valid @RequestBody PriceQuoteRequest req){ return pricing.quote(req); }


    @GetMapping("/convert")
    public java.util.Map<String,Object> convert(@RequestParam int inr, @RequestParam String to){
        int v = pricing.convertInrTo(to, inr);
        return java.util.Map.of("amount", v, "currency", to.toUpperCase());
    }
}