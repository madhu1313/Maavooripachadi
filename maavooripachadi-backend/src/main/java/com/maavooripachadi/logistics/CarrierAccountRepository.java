package com.maavooripachadi.logistics;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CarrierAccountRepository extends JpaRepository<CarrierAccount, Long> {
    Optional<CarrierAccount> findFirstByCarrierAndEnabledTrue(String carrier);
}