package com.maavooripachadi.support;

import com.maavooripachadi.support.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/support")
public class SupportPublicController {
    private final SupportService svc;
    public SupportPublicController(SupportService svc){ this.svc=svc; }

    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public SupportTicket open(@RequestBody @Valid CreateTicketRequest req){ return svc.open(req); }

    @PostMapping("/message")
    public TicketMessage message(@RequestBody @Valid AddMessageRequest req){ return svc.addMessage(req); }

    @PostMapping("/csat")
    public CsatSurvey csat(@RequestBody @Valid CsatSubmitRequest req){ return svc.submitCsat(req); }
}
