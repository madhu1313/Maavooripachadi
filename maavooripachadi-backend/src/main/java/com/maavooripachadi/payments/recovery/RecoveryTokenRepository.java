package com.maavooripachadi.payments.recovery;


import org.springframework.data.jpa.repository.JpaRepository;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


public interface RecoveryTokenRepository extends JpaRepository<RecoveryToken, Long> {
    Optional<RecoveryToken> findByTokenAndConsumedFalse(String token);
    List<RecoveryToken> findByConsumedFalseAndExpiresAtBefore(OffsetDateTime threshold);
}