package com.maavooripachadi.pricing;

import com.maavooripachadi.catalog.Variant;
import com.maavooripachadi.catalog.VariantRepository;
import com.maavooripachadi.pricing.dto.PriceQuoteItem;
import com.maavooripachadi.pricing.dto.PriceQuoteRequest;
import com.maavooripachadi.pricing.dto.PriceQuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    @Mock private PriceListRepository priceListRepository;
    @Mock private PriceListItemRepository priceListItemRepository;
    @Mock private CouponRepository couponRepository;
    @Mock private TaxRuleRepository taxRuleRepository;
    @Mock private ShippingRuleRepository shippingRuleRepository;
    @Mock private CurrencyRateRepository currencyRateRepository;
    @Mock private VariantRepository variantRepository;

    private PricingService service;

    @BeforeEach
    void setUp() {
        service = new PricingService(
            priceListRepository,
            priceListItemRepository,
            couponRepository,
            taxRuleRepository,
            shippingRuleRepository,
            currencyRateRepository,
            variantRepository
        );
    }

    @Test
    void quoteAppliesPriceListDiscountTaxAndShipping() {
        PriceList priceList = new PriceList();
        ReflectionTestUtils.setField(priceList, "id", 1L);
        priceList.setCurrency("USD");
        when(priceListRepository.findByNameAndActiveTrue("B2B")).thenReturn(Optional.of(priceList));

        PriceListItem listItem = new PriceListItem();
        listItem.setPricePaise(1500);
        when(priceListItemRepository.findByPriceListIdAndVariantId(1L, 42L)).thenReturn(Optional.of(listItem));

        Coupon coupon = new Coupon();
        coupon.setActive(true);
        coupon.setCode("SAVE10");
        coupon.setType(CouponType.PERCENT);
        coupon.setValue(10);
        coupon.setUsageCount(1);
        coupon.setUsageLimit(10);
        coupon.setStartsAt(OffsetDateTime.now().minusDays(1));
        when(couponRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(coupon));

        TaxRule taxRule = new TaxRule();
        taxRule.setActive(true);
        taxRule.setRegionPattern("US-.*");
        taxRule.setRatePercent(7);
        when(taxRuleRepository.findAll()).thenReturn(List.of(taxRule));

        ShippingRule shippingRule = new ShippingRule();
        shippingRule.setActive(true);
        shippingRule.setPincodePattern("\\d{5}");
        shippingRule.setMinSubtotalPaise(0);
        shippingRule.setFeePaise(600);
        when(shippingRuleRepository.findAll()).thenReturn(List.of(shippingRule));

        PriceQuoteItem item = new PriceQuoteItem();
        item.setVariantId(42L);
        item.setQty(2);

        PriceQuoteRequest request = new PriceQuoteRequest();
        request.setItems(List.of(item));
        request.setPriceListName("B2B");
        request.setPincode("56001");
        request.setCouponCode("SAVE10");
        request.setRegion("US-NY");

        PriceQuoteResponse response = service.quote(request);

        int subtotal = 2 * 1500;
        int expectedDiscount = Math.round(subtotal * 0.10f);
        int taxable = subtotal - expectedDiscount;
        int expectedTax = Math.round(taxable * 0.07f);

        assertThat(response.getSubtotalPaise()).isEqualTo(subtotal);
        assertThat(response.getDiscountPaise()).isEqualTo(expectedDiscount);
        assertThat(response.getTaxPaise()).isEqualTo(expectedTax);
        assertThat(response.getShippingPaise()).isEqualTo(600);
        assertThat(response.getTotalPaise()).isEqualTo(subtotal - expectedDiscount + expectedTax + 600);
        assertThat(response.getCurrency()).isEqualTo("USD");
    }

    @Test
    void quoteFallsBackToVariantPriceAndDefaults() {
        when(priceListRepository.findFirstByIsDefaultTrueAndActiveTrue()).thenReturn(Optional.empty());

        Variant variant = new Variant();
        variant.setPricePaise(2000);
        when(variantRepository.findById(7L)).thenReturn(Optional.of(variant));

        PriceQuoteItem item = new PriceQuoteItem();
        item.setVariantId(7L);
        item.setQty(1);
        PriceQuoteRequest request = new PriceQuoteRequest();
        request.setItems(List.of(item));
        request.setPincode("500001");

        PriceQuoteResponse response = service.quote(request);

        assertThat(response.getSubtotalPaise()).isEqualTo(2000);
        assertThat(response.getCurrency()).isEqualTo("INR");
    }

    @Test
    void convertInrToUsesFxRateAndRounds() {
        CurrencyRate rate = new CurrencyRate();
        rate.setRate(0.012); // INR to USD
        when(currencyRateRepository.findByFromAndTo("INR", "USD")).thenReturn(Optional.of(rate));

        int cents = service.convertInrTo("USD", 10_000); // Rs 100

        assertThat(cents).isEqualTo((int) Math.round((10_000 / 100.0) * 0.012 * 100));
    }

    @Test
    void convertInrToDefaultsWhenRateMissing() {
        when(currencyRateRepository.findByFromAndTo(any(), any())).thenReturn(Optional.empty());

        int fallback = service.convertInrTo("XYZ", 4_200);
        assertThat(fallback).isEqualTo((int) Math.round((4_200 / 100.0) * 0.012 * 100));
        assertThat(service.convertInrTo("INR", 9_999)).isEqualTo(9_999);
    }
}
