package com.maavooripachadi.risk.dto;

import com.maavooripachadi.risk.RuleOperator;

public class RuleRequest {
    private String name; private RuleOperator operator; private String leftKey; private String rightValue; private int scoreImpact; private int priority; private Boolean active;
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
