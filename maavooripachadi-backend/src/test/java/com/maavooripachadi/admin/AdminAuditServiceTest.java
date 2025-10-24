package com.maavooripachadi.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminAuditServiceTest {

  private AuditLogRepository repository;
  private AdminAuditService service;

  @BeforeEach
  void setUp() {
    repository = mock(AuditLogRepository.class);
    service = new AdminAuditService(repository);
  }

  @Test
  void logStoresAuditEntryAndDefaultsActor() {
    service.log(null, "PING", "Admin ping OK");

    ArgumentCaptor<AuditLog> logCaptor = ArgumentCaptor.forClass(AuditLog.class);
    verify(repository).save(logCaptor.capture());
    AuditLog saved = logCaptor.getValue();
    assertThat(saved.getActor()).isEqualTo("system");
    assertThat(saved.getAction()).isEqualTo("PING");
    assertThat(saved.getDetail()).isEqualTo("Admin ping OK");
  }
}
