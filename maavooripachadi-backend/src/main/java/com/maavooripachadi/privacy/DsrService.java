package com.maavooripachadi.privacy;


import com.maavooripachadi.privacy.dto.DsrDecisionRequest;
import com.maavooripachadi.privacy.dto.DsrOpenRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DsrService {
    private final DsrRequestRepository repo; private final PrivacyEventRepository events;
    public DsrService(DsrRequestRepository repo, PrivacyEventRepository events){ this.repo = repo; this.events = events; }


    @Transactional
    public DsrRequest open(DsrOpenRequest req){
        DsrRequest d = new DsrRequest(); d.setType(req.getType()); d.setSubjectId(req.getSubjectId()); d.setDetails(req.getDetails());
        DsrRequest saved = repo.save(d);
        PrivacyEvent e = new PrivacyEvent(); e.setSubjectId(req.getSubjectId()); e.setKind("DSR_OPENED"); e.setPayloadJson("{\"type\":\""+req.getType()+"\"}"); events.save(e);
        return saved;
    }


    @Transactional
    public DsrRequest decide(Long id, DsrDecisionRequest req){
        DsrRequest d = repo.findById(id).orElseThrow();
        d.setStatus(req.getStatus());
        if (req.getDetails()!=null) d.setDetails(req.getDetails());
        DsrRequest saved = repo.save(d);
        PrivacyEvent e = new PrivacyEvent(); e.setSubjectId(d.getSubjectId()); e.setKind("DSR_DECIDED"); e.setPayloadJson("{\"id\":"+id+",\"status\":\""+req.getStatus()+"\"}"); events.save(e);
        return saved;
    }
}