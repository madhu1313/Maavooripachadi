package com.maavooripachadi.pricing;


import com.maavooripachadi.pricing.dto.SetRateRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/pricing")
@Validated
public class PricingAdminController {
    private final PriceListRepository priceLists; private final PriceListItemRepository items; private final CouponRepository coupons; private final TaxRuleRepository taxes; private final ShippingRuleRepository ships; private final FxService fx;
    public PricingAdminController(PriceListRepository p, PriceListItemRepository i, CouponRepository c, TaxRuleRepository t, ShippingRuleRepository s, FxService fx){ this.priceLists=p; this.items=i; this.coupons=c; this.taxes=t; this.ships=s; this.fx=fx; }


    // Price Lists
    @PostMapping("/price-list")
    @PreAuthorize("hasAuthority('PRICING_WRITE') or hasRole('ADMIN')")
    public PriceList createPL(@RequestBody PriceList pl){ return priceLists.save(pl); }


    @PostMapping("/price-list/{plId}/item")
    @PreAuthorize("hasAuthority('PRICING_WRITE') or hasRole('ADMIN')")
    public PriceListItem upsertItem(@PathVariable Long plId, @RequestBody PriceListItem body){ var pl = priceLists.findById(plId).orElseThrow(); body.setPriceList(pl); return items.save(body); }


    // Coupons
    @PostMapping("/coupon")
    @PreAuthorize("hasAuthority('PRICING_WRITE') or hasRole('ADMIN')")
    public Coupon saveCoupon(@RequestBody Coupon c){ return coupons.save(c); }


    // Tax rules
    @PostMapping("/tax")
    @PreAuthorize("hasAuthority('PRICING_WRITE') or hasRole('ADMIN')")
    public TaxRule saveTax(@RequestBody TaxRule t){ return taxes.save(t); }


    // Shipping rules
    @PostMapping("/shipping")
    @PreAuthorize("hasAuthority('PRICING_WRITE') or hasRole('ADMIN')")
    public ShippingRule saveShip(@RequestBody ShippingRule s){ return ships.save(s); }


    // FX rate
    @PostMapping("/fx")
    @PreAuthorize("hasAuthority('PRICING_WRITE') or hasRole('ADMIN')")
    public CurrencyRate setFx(@Valid @RequestBody SetRateRequest req){ return fx.setRate(req); }
}