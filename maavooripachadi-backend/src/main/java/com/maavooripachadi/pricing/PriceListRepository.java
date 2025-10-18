package com.maavooripachadi.pricing;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    Optional<PriceList> findByNameAndActiveTrue(String name);
    Optional<PriceList> findFirstByIsDefaultTrueAndActiveTrue();
}