package com.maavooripachadi.support.dto;

import jakarta.validation.constraints.*;

public class AssignAgentRequest {
    @NotBlank private String ticketNo; @NotBlank private String agent;
    public String getTicketNo() { return ticketNo; }
    public void setTicketNo(String ticketNo) { this.ticketNo = ticketNo; }
    public String getAgent() { return agent; }
    public void setAgent(String agent) { this.agent = agent; }
}
