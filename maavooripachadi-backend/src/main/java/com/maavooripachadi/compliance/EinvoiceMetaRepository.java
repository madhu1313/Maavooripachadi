package com.maavooripachadi.compliance;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface EinvoiceMetaRepository extends JpaRepository<EinvoiceMeta, Long> {
    Optional<EinvoiceMeta> findByOrderNo(String orderNo);
}