package com.maavooripachadi.disputes;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;


public interface DisputeRepository extends JpaRepository<Dispute, Long> {
  Optional<Dispute> findByProviderCaseId(String providerCaseId);


  @Query("""
SELECT d FROM Dispute d
WHERE (:gateway IS NULL OR d.gateway = :gateway)
AND (:status IS NULL OR d.status = :status)
AND (:q IS NULL OR LOWER(d.orderNo) LIKE LOWER(CONCAT('%', :q, '%'))
OR LOWER(d.providerCaseId) LIKE LOWER(CONCAT('%', :q, '%')))
""")
  Page<Dispute> search(String gateway, DisputeStatus status, String q, Pageable pageable);
}