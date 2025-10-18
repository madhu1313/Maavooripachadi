package com.maavooripachadi.pricing;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    Optional<CurrencyRate> findByFromAndTo(String from, String to);
}