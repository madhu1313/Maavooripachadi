package com.maavooripachadi.risk;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DenyListRepository extends JpaRepository<DenyListEntry, Long> {
    Optional<DenyListEntry> findByTypeAndValue(DenyType type, String value);
}
