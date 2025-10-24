package com.maavooripachadi.pricing;

import com.maavooripachadi.pricing.dto.SetRateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PricingAdminControllerTest {

    private PriceListRepository priceListRepository;
    private PriceListItemRepository priceListItemRepository;
    private CouponRepository couponRepository;
    private TaxRuleRepository taxRuleRepository;
    private ShippingRuleRepository shippingRuleRepository;
    private FxService fxService;
    private PricingAdminController controller;

    @BeforeEach
    void setUp() {
        priceListRepository = mock(PriceListRepository.class);
        priceListItemRepository = mock(PriceListItemRepository.class);
        couponRepository = mock(CouponRepository.class);
        taxRuleRepository = mock(TaxRuleRepository.class);
        shippingRuleRepository = mock(ShippingRuleRepository.class);
        fxService = mock(FxService.class);

        controller = new PricingAdminController(
            priceListRepository,
            priceListItemRepository,
            couponRepository,
            taxRuleRepository,
            shippingRuleRepository,
            fxService
        );
    }

    @Test
    void createPriceListSavesEntity() {
        PriceList priceList = new PriceList();
        when(priceListRepository.save(priceList)).thenReturn(priceList);

        assertThat(controller.createPL(priceList)).isSameAs(priceList);
        verify(priceListRepository).save(priceList);
    }

    @Test
    void upsertItemAssociatesPriceListAndSaves() {
        PriceList priceList = new PriceList();
        when(priceListRepository.findById(10L)).thenReturn(Optional.of(priceList));

        PriceListItem item = new PriceListItem();
        when(priceListItemRepository.save(item)).thenReturn(item);

        PriceListItem result = controller.upsertItem(10L, item);

        assertThat(result).isSameAs(item);
        assertThat(item.getPriceList()).isSameAs(priceList);
        verify(priceListItemRepository).save(item);
    }

    @Test
    void saveCouponDelegatesToRepository() {
        Coupon coupon = new Coupon();
        when(couponRepository.save(coupon)).thenReturn(coupon);

        assertThat(controller.saveCoupon(coupon)).isSameAs(coupon);
        verify(couponRepository).save(coupon);
    }

    @Test
    void saveTaxDelegatesToRepository() {
        TaxRule rule = new TaxRule();
        when(taxRuleRepository.save(rule)).thenReturn(rule);

        assertThat(controller.saveTax(rule)).isSameAs(rule);
        verify(taxRuleRepository).save(rule);
    }

    @Test
    void saveShippingDelegatesToRepository() {
        ShippingRule rule = new ShippingRule();
        when(shippingRuleRepository.save(rule)).thenReturn(rule);

        assertThat(controller.saveShip(rule)).isSameAs(rule);
        verify(shippingRuleRepository).save(rule);
    }

    @Test
    void setFxDelegatesToFxService() {
        SetRateRequest request = new SetRateRequest();
        CurrencyRate rate = new CurrencyRate();
        when(fxService.setRate(request)).thenReturn(rate);

        assertThat(controller.setFx(request)).isSameAs(rate);
        verify(fxService).setRate(request);
    }
}
