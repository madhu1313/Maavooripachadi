package com.maavooripachadi.support.dto;

import com.maavooripachadi.support.TicketChannel;
import com.maavooripachadi.support.TicketPriority;
import jakarta.validation.constraints.*;

public class CreateTicketRequest {
    @NotBlank private String subject;
    @NotBlank private String description;
    @Email private String requesterEmail;
    private String requesterName;
    private TicketPriority priority = TicketPriority.MEDIUM;
    private TicketChannel channel = TicketChannel.WEB;

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequesterEmail() { return requesterEmail; }
    public void setRequesterEmail(String requesterEmail) { this.requesterEmail = requesterEmail; }
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }
    public TicketChannel getChannel() { return channel; }
    public void setChannel(TicketChannel channel) { this.channel = channel; }
}
