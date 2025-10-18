package com.maavooripachadi.support;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class SlaService {
    private final TicketSlaRepository repo;
    private final SupportProperties props;
    public SlaService(TicketSlaRepository repo, SupportProperties props){ this.repo = repo; this.props = props; }

    public void applyDefaults(SupportTicket t){
        TicketSla sla = repo.findAll().stream().findFirst().orElseGet(() -> {
            TicketSla s = new TicketSla(); s.setName(props.getDefaultSlaName()); s.setFirstResponseMins(120); s.setResolveMins(2880); return repo.save(s);
        });
        t.setFirstResponseDueAt(OffsetDateTime.now().plusMinutes(sla.getFirstResponseMins()));
        t.setResolveDueAt(OffsetDateTime.now().plusMinutes(sla.getResolveMins()));
    }
}
