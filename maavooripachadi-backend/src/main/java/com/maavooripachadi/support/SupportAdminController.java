package com.maavooripachadi.support;

import com.maavooripachadi.support.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/support")
public class SupportAdminController {
    private final SupportService svc; private final CannedResponseService cannedSvc; private final SupportTicketRepository tickets; private final TicketMessageRepository messages;
    public SupportAdminController(SupportService svc, CannedResponseService cannedSvc, SupportTicketRepository tickets, TicketMessageRepository messages){ this.svc=svc; this.cannedSvc=cannedSvc; this.tickets=tickets; this.messages=messages; }

    @GetMapping("/tickets")
    @PreAuthorize("hasAuthority('SUPPORT_READ') or hasRole('ADMIN')")
    public Page<SupportTicket> list(@RequestParam(value = "status", defaultValue="OPEN") TicketStatus status,
                                    @RequestParam(value = "page", defaultValue="0") int page,
                                    @RequestParam(value = "size", defaultValue="20") int size){ return svc.list(status, page, size); }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('SUPPORT_WRITE') or hasRole('ADMIN')")
    public SupportTicket assign(@RequestBody AssignAgentRequest req){ return svc.assign(req); }

    @PostMapping("/status")
    @PreAuthorize("hasAuthority('SUPPORT_WRITE') or hasRole('ADMIN')")
    public SupportTicket status(@RequestBody UpdateStatusRequest req){ return svc.updateStatus(req); }

    @PostMapping("/tag")
    @PreAuthorize("hasAuthority('SUPPORT_WRITE') or hasRole('ADMIN')")
    public SupportTicket tag(@RequestBody AddTagRequest req){ return svc.addTag(req); }

    @PostMapping("/canned")
    @PreAuthorize("hasAuthority('SUPPORT_WRITE') or hasRole('ADMIN')")
    public CannedResponse upsert(@RequestBody CannedUpsertRequest req){ return cannedSvc.upsert(req); }

    @GetMapping("/tickets/{ticketNo}/messages")
    @PreAuthorize("hasAuthority('SUPPORT_READ') or hasRole('ADMIN')")
    public java.util.List<TicketMessage> messages(@PathVariable String ticketNo){ var t = tickets.findByTicketNo(ticketNo).orElseThrow(); return messages.findByTicketIdOrderByCreatedAtAsc(t.getId()); }
}
