package com.maavooripachadi.content;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/content")
@RequiredArgsConstructor
public class ContentApprovalController {
  private final ContentApprovalService svc;

  @PostMapping("/{type}/{id}/submit")
  public Map<String, Object> submit(@PathVariable String type, @PathVariable long id) {
    svc.submit(type, id, "system");
    return Map.of("status", "IN_REVIEW");
  }

  @PostMapping("/{type}/{id}/decide")
  public Map<String, Object> decide(@PathVariable String type,
                                    @PathVariable long id,
                                    @RequestParam String decision) {
    svc.decide(id, decision, "admin", null);
    return Map.of("status", decision);
  }
}
