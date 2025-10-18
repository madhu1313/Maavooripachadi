package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.EvaluateRequest;
import com.maavooripachadi.risk.dto.EvaluateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskEngineService {

    private final DenyListService deny;
    private final VelocityService velocity;
    private final RuleEngine rules;
    private final RiskEventRepository events;
    private final RiskScoreRepository scores;
    private final RiskCaseRepository cases;

    public RiskEngineService(DenyListService deny, VelocityService velocity, RuleEngine rules, RiskEventRepository events, RiskScoreRepository scores, RiskCaseRepository cases){
        this.deny = deny; this.velocity = velocity; this.rules = rules; this.events = events; this.scores = scores; this.cases = cases;
    }

    @Transactional
    public EvaluateResponse evaluate(EvaluateRequest req){
        // 1) Persist event
        RiskEvent evt = new RiskEvent();
        evt.setSource(req.getSource()); evt.setSubjectId(req.getSubjectId()); evt.setEmail(req.getEmail()); evt.setPhone(req.getPhone());
        evt.setIp(req.getIp()); evt.setDeviceId(req.getDeviceId()); evt.setOrderNo(req.getOrderNo()); evt.setAmountPaise(req.getAmountPaise()); evt.setCurrency(req.getCurrency()); evt.setPayloadJson(req.getPayloadJson());
        evt = events.save(evt);

        List<String> reasons = new ArrayList<>();

        // 2) Deny lists (hard block)
        if (req.getEmail()!=null && deny.isDenied(DenyType.EMAIL, req.getEmail())) reasons.add("deny:email");
        if (req.getIp()!=null && deny.isDenied(DenyType.IP, req.getIp())) reasons.add("deny:ip");
        if (req.getCardFingerprint()!=null && deny.isDenied(DenyType.CARD_FINGERPRINT, req.getCardFingerprint())) reasons.add("deny:card");

        // 3) Velocity
        String velo = velocity.check(req.getIp(), req.getEmail(), req.getDeviceId());
        if (velo != null) reasons.add(velo);

        // 4) Rules (+ mock IP reputation)
        String ipRep = "0"; // TODO integrate with IP intelligence provider
        RuleEngine.Result rr = rules.eval(req, ipRep);
        int score = rr.score; reasons.addAll(rr.reasons);

        // 5) Decision thresholds
        RiskDecision decision;
        if (reasons.stream().anyMatch(s -> s.startsWith("deny:"))) decision = RiskDecision.BLOCK;
        else if (score >= 50 || velo != null) decision = RiskDecision.REVIEW; // tune thresholds
        else decision = RiskDecision.ALLOW;

        // 6) Save score and maybe create case
        RiskScore sc = new RiskScore(); sc.setEvent(evt); sc.setScore(score); sc.setDecision(decision); sc.setReasonsJson(new com.fasterxml.jackson.databind.ObjectMapper().valueToTree(reasons).toString());
        scores.save(sc);

        Long caseId = null;
        if (decision != RiskDecision.ALLOW){
            RiskCase c = new RiskCase(); c.setEvent(evt); c.setStatus(CaseStatus.OPEN); c = cases.save(c); caseId = c.getId();
        }

        EvaluateResponse resp = new EvaluateResponse();
        resp.setScore(score); resp.setDecision(decision); resp.setReasons(reasons.toArray(new String[0])); resp.setCaseId(caseId); resp.setEventId(evt.getId());
        return resp;
    }
}
