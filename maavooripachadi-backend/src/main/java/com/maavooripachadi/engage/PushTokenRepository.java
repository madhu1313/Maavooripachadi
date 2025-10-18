package com.maavooripachadi.engage;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    Optional<PushToken> findByToken(String token);
    Optional<PushToken> findByDeviceId(String deviceId);
}