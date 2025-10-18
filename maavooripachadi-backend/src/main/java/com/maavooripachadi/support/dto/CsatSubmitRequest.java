package com.maavooripachadi.support.dto;

import jakarta.validation.constraints.*;

public class CsatSubmitRequest {
    @NotBlank private String ticketNo; @Min(1) @Max(5) private int rating; private String comment;
    public String getTicketNo() { return ticketNo; }
    public void setTicketNo(String ticketNo) { this.ticketNo = ticketNo; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
