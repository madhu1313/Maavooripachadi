package com.maavooripachadi.compliance;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface EwaybillRepository extends JpaRepository<Ewaybill, Long> {
    Optional<Ewaybill> findByOrderNo(String orderNo);
}