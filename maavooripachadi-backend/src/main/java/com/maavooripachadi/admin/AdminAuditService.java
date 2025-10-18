package com.maavooripachadi.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuditService {
  private final AuditLogRepository repo;

  public void log(String actor, String action, String detail) {
    var auditLog = new AuditLog();
    auditLog.setActor(actor == null ? "system" : actor);
    auditLog.setAction(action);
    auditLog.setDetail(detail);
    repo.save(auditLog);
  }
}
