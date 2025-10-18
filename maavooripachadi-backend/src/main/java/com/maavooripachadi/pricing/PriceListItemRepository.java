package com.maavooripachadi.pricing;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PriceListItemRepository extends JpaRepository<PriceListItem, Long> {
    Optional<PriceListItem> findByPriceListIdAndVariantId(Long priceListId, Long variantId);
}