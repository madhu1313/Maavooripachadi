package com.maavooripachadi.risk;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "risk_rule")
public class RiskRule extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleOperator operator; // how to compare

    @Column(nullable = false)
    private String leftKey; // e.g., amountPaise, ipReputation

    @Column(nullable = false)
    private String rightValue; // e.g., 100000 for Rs 1000

    private int scoreImpact = 10; // adds to score when matched
    private int priority = 100; // smaller executes first
    private Boolean active = Boolean.TRUE;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public RuleOperator getOperator() { return operator; }
    public void setOperator(RuleOperator operator) { this.operator = operator; }
    public String getLeftKey() { return leftKey; }
    public void setLeftKey(String leftKey) { this.leftKey = leftKey; }
    public String getRightValue() { return rightValue; }
    public void setRightValue(String rightValue) { this.rightValue = rightValue; }
    public int getScoreImpact() { return scoreImpact; }
    public void setScoreImpact(int scoreImpact) { this.scoreImpact = scoreImpact; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
