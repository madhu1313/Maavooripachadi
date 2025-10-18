package com.maavooripachadi.privacy;


import com.maavooripachadi.privacy.dto.DsrDecisionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/privacy")
@Validated
public class PrivacyAdminController {
    private final PolicyService policies; private final DsrService dsr;
    public PrivacyAdminController(PolicyService p, DsrService d){ this.policies = p; this.dsr = d; }


    @PostMapping("/policy")
    @PreAuthorize("hasAuthority('ADMIN_PRIVACY') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public PrivacyPolicy publish(@RequestBody PrivacyPolicy policy){ return policies.publish(policy); }


    @PostMapping("/dsr/{id}/decide")
    @PreAuthorize("hasAuthority('ADMIN_PRIVACY') or hasRole('ADMIN')")
    public DsrRequest decide(@PathVariable Long id, @Valid @RequestBody DsrDecisionRequest req){ return dsr.decide(id, req); }
}