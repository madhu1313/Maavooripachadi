package com.maavooripachadi.reviews;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface ProductRatingAggRepository extends JpaRepository<ProductRatingAgg, Long> {
    Optional<ProductRatingAgg> findByProductIdAndVariantId(Long productId, Long variantId);
}