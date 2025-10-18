package com.maavooripachadi.security;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityAuditLogRepository extends JpaRepository<AuditLog, Long> { }
