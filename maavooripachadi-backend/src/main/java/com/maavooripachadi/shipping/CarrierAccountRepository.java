package com.maavooripachadi.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("shippingCarrierAccountRepository")
public interface CarrierAccountRepository extends JpaRepository<CarrierAccount, Long> { }
