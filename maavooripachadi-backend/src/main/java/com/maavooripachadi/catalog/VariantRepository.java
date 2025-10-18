package com.maavooripachadi.catalog;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface VariantRepository extends JpaRepository<Variant, Long> {
    List<Variant> findByProductId(Long productId);
}