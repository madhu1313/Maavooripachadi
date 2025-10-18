package com.maavooripachadi.support.dto;

import com.maavooripachadi.support.MessageVisibility;
import jakarta.validation.constraints.*;

public class AddMessageRequest {
    @NotBlank private String ticketNo;
    @NotBlank private String author;
    @NotBlank private String body;
    private MessageVisibility visibility = MessageVisibility.PUBLIC;

    public String getTicketNo() { return ticketNo; }
    public void setTicketNo(String ticketNo) { this.ticketNo = ticketNo; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public MessageVisibility getVisibility() { return visibility; }
    public void setVisibility(MessageVisibility visibility) { this.visibility = visibility; }
}
