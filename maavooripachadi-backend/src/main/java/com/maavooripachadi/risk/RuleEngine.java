package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.EvaluateRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RuleEngine {
    private final RiskRuleRepository rules;
    public RuleEngine(RiskRuleRepository rules){ this.rules = rules; }

    public static class Result { public int score; public List<String> reasons = new ArrayList<>(); }

    public Result eval(EvaluateRequest req, String ipReputation){
        Result r = new Result();
        for (RiskRule rule : rules.findAllByActiveTrueOrderByPriorityAsc()){
            if (match(rule, req, ipReputation)){
                r.score += rule.getScoreImpact();
                r.reasons.add(rule.getName());
            }
        }
        return r;
    }

    private boolean match(RiskRule rule, EvaluateRequest req, String ipRep){
        String left = switch (rule.getLeftKey()){
            case "amountPaise" -> String.valueOf(req.getAmountPaise()==null?0:req.getAmountPaise());
            case "ipReputation" -> ipRep==null?"0":ipRep;
            case "email" -> req.getEmail();
            default -> null;
        };
        String right = rule.getRightValue();
        if (left == null || right == null) return false;
        try {
            return switch (rule.getOperator()){
                case GT -> Double.parseDouble(left) > Double.parseDouble(right);
                case GTE -> Double.parseDouble(left) >= Double.parseDouble(right);
                case LT -> Double.parseDouble(left) < Double.parseDouble(right);
                case LTE -> Double.parseDouble(left) <= Double.parseDouble(right);
                case EQ -> left.equalsIgnoreCase(right);
                case REGEX -> left.matches(right);
                case IN -> java.util.Arrays.stream(right.split(",")).anyMatch(s -> s.trim().equalsIgnoreCase(left));
            };
        } catch (Exception e){ return false; }
    }
}
