package com.maavooripachadi.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShippingZoneRepository extends JpaRepository<ShippingZone, Long> {
    Optional<ShippingZone> findByName(String name);
}
