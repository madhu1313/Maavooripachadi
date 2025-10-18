package com.maavooripachadi.support.dto;

import com.maavooripachadi.support.TicketStatus;
import jakarta.validation.constraints.*;

public class UpdateStatusRequest {
    @NotBlank private String ticketNo; @NotNull private TicketStatus status; private String note;
    public String getTicketNo() { return ticketNo; }
    public void setTicketNo(String ticketNo) { this.ticketNo = ticketNo; }
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
