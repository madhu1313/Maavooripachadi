package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.DenyListUpsertRequest;
import com.maavooripachadi.risk.dto.RuleRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/risk")
public class RiskAdminController {

    private final DenyListService deny;
    private final DenyListRepository denyRepo;
    private final VelocityWindowRepository velRepo;
    private final RiskRuleRepository ruleRepo;
    private final RiskCaseRepository caseRepo;

    public RiskAdminController(DenyListService deny, DenyListRepository denyRepo, VelocityWindowRepository velRepo, RiskRuleRepository ruleRepo, RiskCaseRepository caseRepo){
        this.deny = deny; this.denyRepo = denyRepo; this.velRepo = velRepo; this.ruleRepo = ruleRepo; this.caseRepo = caseRepo;
    }

    @PostMapping("/deny")
    @PreAuthorize("hasAuthority('RISK_WRITE') or hasRole('ADMIN')")
    public DenyListEntry upsert(@RequestBody DenyListUpsertRequest req){ return deny.upsert(req); }

    @PostMapping("/velocity")
    @PreAuthorize("hasAuthority('RISK_WRITE') or hasRole('ADMIN')")
    public VelocityWindow saveVel(@RequestBody VelocityWindow v){ return velRepo.save(v); }

    @PostMapping("/rule")
    @PreAuthorize("hasAuthority('RISK_WRITE') or hasRole('ADMIN')")
    public RiskRule saveRule(@RequestBody RuleRequest r){
        RiskRule rr = new RiskRule(); rr.setName(r.getName()); rr.setOperator(r.getOperator()); rr.setLeftKey(r.getLeftKey()); rr.setRightValue(r.getRightValue()); rr.setScoreImpact(r.getScoreImpact()); rr.setPriority(r.getPriority()); rr.setActive(r.getActive());
        return ruleRepo.save(rr);
    }

    @PostMapping("/case/{id}/close")
    @PreAuthorize("hasAuthority('RISK_WRITE') or hasRole('ADMIN')")
    public RiskCase close(@PathVariable Long id, @RequestParam(value = "notes", required=false) String notes){
        RiskCase c = caseRepo.findById(id).orElseThrow(); c.setStatus(CaseStatus.CLOSED); if (notes!=null) c.setNotes(notes); return caseRepo.save(c);
    }
}
