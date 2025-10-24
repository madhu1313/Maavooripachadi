package com.maavooripachadi.admin;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminControllerTest {

  private SimpleMeterRegistry meterRegistry;
  private AdminRateLimitGuard rateLimitGuard;
  private AdminAuditService auditService;
  private AdminController controller;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();
    rateLimitGuard = mock(AdminRateLimitGuard.class);
    auditService = mock(AdminAuditService.class);
    controller = new AdminController(meterRegistry, rateLimitGuard, auditService);
  }

  @Test
  void pingChecksRateLimitLogsAuditAndReturnsTimestamp() {
    Map<String, Object> response = controller.ping("alice");

    verify(rateLimitGuard).check("/ping", "alice");
    verify(auditService).log("alice", "PING", "Admin ping OK");
    assertThat(response).containsEntry("ok", true);
    assertThat(response.get("ts")).asString().isNotEmpty();
  }

  @Test
  void statsReadsCountersAndLogsAudit() {
    meterRegistry.counter("checkout.start").increment(5);
    meterRegistry.counter("payment.success").increment(3);
    meterRegistry.counter("webhook.ok").increment(7);

    AdminStatsResponse response = controller.stats("bob");

    verify(rateLimitGuard).check("/stats", "bob");
    verify(auditService).log("bob", "STATS_VIEW", "Metrics snapshot exported");
    assertThat(response.checkoutStarts()).isEqualTo(5.0);
    assertThat(response.paymentSuccess()).isEqualTo(3.0);
    assertThat(response.webhooksOk()).isEqualTo(7.0);
  }

  @Test
  void leaveNoteAuditsMessage() {
    Map<String, Object> response = controller.leaveNote("carol", "Investigate order delays");

    verify(rateLimitGuard).check("/note", "carol");
    verify(auditService).log("carol", "ADMIN_NOTE", "Investigate order delays");
    assertThat(response).containsEntry("ok", true);
  }
}
