package com.maavooripachadi.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;

public interface RiskEventRepository extends JpaRepository<RiskEvent, Long> {
    @Query("select count(e) from RiskEvent e where e.createdAt >= ?1 and (e.ip = ?2 or e.email = ?3 or e.deviceId = ?4)")
    long countSinceForAny(OffsetDateTime since, String ip, String email, String deviceId);
}
