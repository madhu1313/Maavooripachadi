package com.maavooripachadi.logistics;


import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional;


public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByShipmentNo(String shipmentNo);
    List<Shipment> findByOrderNo(String orderNo);
    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);
}