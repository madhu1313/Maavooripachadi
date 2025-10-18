package com.maavooripachadi.support.dto;

import jakarta.validation.constraints.*;

public class AddTagRequest {
    @NotBlank private String ticketNo; @NotBlank private String tag;
    public String getTicketNo() { return ticketNo; }
    public void setTicketNo(String ticketNo) { this.ticketNo = ticketNo; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
