package com.maavooripachadi.engage;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface OutboundTemplateRepository extends JpaRepository<OutboundTemplate, Long> {
    Optional<OutboundTemplate> findByCode(String code);
}