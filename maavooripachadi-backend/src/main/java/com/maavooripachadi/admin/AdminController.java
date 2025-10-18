package com.maavooripachadi.admin;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
  private final MeterRegistry meters;
  private final AdminRateLimitGuard rate;
  private final AdminAuditService audit;
  @GetMapping("/ping")
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN_READ')")
  public Map<String, Object> ping(@RequestHeader(value = "X-Requestor",
          required = false) String actor){
    rate.check("/ping", actor);
    audit.log(actor, "PING", "Admin ping OK");
    return Map.of("ok", true, "ts", OffsetDateTime.now().toString());
  }
  @GetMapping("/stats")
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN_READ')")
  public AdminStatsResponse stats(@RequestHeader(value = "X-Requestor", required = false) String actor){
    rate.check("/stats", actor);
    audit.log(actor, "STATS_VIEW", "Metrics snapshot exported");
    var checkout = meters.counter("checkout.start").count();
    var paid = meters.counter("payment.success").count();
    var webhook = meters.counter("webhook.ok").count();
    return new AdminStatsResponse(checkout, paid, webhook);
  }
  @PostMapping("/note")
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN_WRITE')")
  public Map<String, Object> leaveNote(@RequestHeader(value = "X-Requestor",
                                               required = false) String actor,
                                       @RequestParam @NotBlank String message)
  {
    rate.check("/note", actor);
    audit.log(actor, "ADMIN_NOTE", message);
    return Map.of("ok", true);
  }
}