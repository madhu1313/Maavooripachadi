package com.maavooripachadi.support;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CannedResponseRepository extends JpaRepository<CannedResponse, Long> {
    Optional<CannedResponse> findByKeyName(String keyName);
}
