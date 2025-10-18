package com.maavooripachadi.pricing;


import com.maavooripachadi.catalog.Variant;
import com.maavooripachadi.catalog.VariantRepository;
import com.maavooripachadi.pricing.dto.PriceQuoteItem;
import com.maavooripachadi.pricing.dto.PriceQuoteRequest;
import com.maavooripachadi.pricing.dto.PriceQuoteResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class PricingService {


    private final PriceListRepository priceLists;
    private final PriceListItemRepository items;
    private final CouponRepository coupons;
    private final TaxRuleRepository taxes;
    private final ShippingRuleRepository shipping;
    private final CurrencyRateRepository fx;
    private final VariantRepository variants;


    public PricingService(PriceListRepository priceLists,
                          PriceListItemRepository items,
                          CouponRepository coupons,
                          TaxRuleRepository taxes,
                          ShippingRuleRepository shipping,
                          CurrencyRateRepository fx,
                          VariantRepository variants) {
        this.priceLists = priceLists; this.items = items; this.coupons = coupons; this.taxes = taxes; this.shipping = shipping; this.fx = fx; this.variants = variants;
    }


    @Transactional(readOnly = true)
    public PriceQuoteResponse quote(PriceQuoteRequest req){
        var priceListOpt = resolvePriceList(req.getPriceListName());
        PriceList pl = priceListOpt.orElse(null);
        int subtotal = 0;
        for (PriceQuoteItem it : req.getItems()){
            int unitPrice = pl != null ? priceForVariant(pl, it.getVariantId()) : variantPrice(it.getVariantId());
            subtotal += unitPrice * it.getQty();
        }
        int discount = computeDiscount(req.getCouponCode(), subtotal);
        int tax = computeTax(req.getRegion(), subtotal - discount);
        int ship = computeShipping(req.getPincode(), subtotal - discount);
        int total = subtotal - discount + tax + ship;
        PriceQuoteResponse r = new PriceQuoteResponse();
        r.setSubtotalPaise(subtotal); r.setDiscountPaise(discount); r.setTaxPaise(tax); r.setShippingPaise(ship); r.setTotalPaise(total); r.setCurrency(pl != null ? pl.getCurrency() : "INR");
        return r;
    }


    @Transactional(readOnly = true)
    public int priceForVariant(PriceList pl, Long variantId){
        Optional<PriceListItem> pli = items.findByPriceListIdAndVariantId(pl.getId(), variantId);
        return pli.map(PriceListItem::getPricePaise).orElseGet(() -> variantPrice(variantId));
    }


    private int variantPrice(Long variantId){
        return variants.findById(variantId).map(Variant::getPricePaise).orElse(0);
    }


    private java.util.Optional<PriceList> resolvePriceList(String name){
        if (name != null && !name.isBlank()){
            return priceLists.findByNameAndActiveTrue(name)
                    .or(() -> priceLists.findFirstByIsDefaultTrueAndActiveTrue());
        }
        return priceLists.findFirstByIsDefaultTrueAndActiveTrue();
    }


    private int computeDiscount(String couponCode, int subtotal){
        if (couponCode == null || couponCode.isBlank()) return 0;
        var now = OffsetDateTime.now();
        return coupons.findByCodeIgnoreCase(couponCode).filter(c -> Boolean.TRUE.equals(c.getActive()))
                .filter(c -> (c.getStartsAt() == null || !now.isBefore(c.getStartsAt())) && (c.getEndsAt() == null || !now.isAfter(c.getEndsAt())))
                .filter(c -> c.getUsageLimit() == null || c.getUsageCount() < c.getUsageLimit())
                .filter(c -> c.getMinSubtotalPaise() == null || subtotal >= c.getMinSubtotalPaise())
                .map(c -> {
                    int d;
                    if (c.getType() == CouponType.FLAT) d = c.getValue();
                    else d = Math.round(subtotal * (c.getValue() / 100.0f));
                    if (c.getMaxDiscountPaise() != null) d = Math.min(d, c.getMaxDiscountPaise());
                    return Math.max(d, 0);
                }).orElse(0);
    }


    private int computeTax(String region, int taxable){
        if (taxable <= 0) return 0;
        int rate = taxes.findAll().stream()
                .filter(t -> Boolean.TRUE.equals(t.getActive()))
                .filter(t -> regionMatches(t.getRegionPattern(), region))
                .mapToInt(TaxRule::getRatePercent)
                .findFirst().orElse(5); // default 5%
        return Math.round(taxable * (rate / 100.0f));
    }


    private boolean regionMatches(String pattern, String region){
        if (pattern == null || pattern.isBlank()) return true;
        if (region == null) return false;
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(region).matches();
    }


    private int computeShipping(String pincode, int netSubtotal){
        return shipping.findAll().stream()
                .filter(r -> Boolean.TRUE.equals(r.getActive()))
                .filter(r -> pincode != null && pincode.matches(r.getPincodePattern()))
                .filter(r -> netSubtotal >= r.getMinSubtotalPaise())
                .mapToInt(ShippingRule::getFeePaise)
                .min().orElse(5000); // default Rs 50
    }


    @Transactional(readOnly = true)
    public int convertInrTo(String toCcy, int inrPaise){
        if ("INR".equalsIgnoreCase(toCcy)) return inrPaise;
        double rate = fx.findByFromAndTo("INR", toCcy.toUpperCase()).map(CurrencyRate::getRate).orElse(0.012);
        long cents = Math.round((inrPaise / 100.0) * rate * 100.0);
        return (int) cents;
    }
}
