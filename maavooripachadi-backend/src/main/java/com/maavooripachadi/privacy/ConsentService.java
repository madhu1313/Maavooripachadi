package com.maavooripachadi.privacy;


import com.maavooripachadi.privacy.dto.ConsentUpsertRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ConsentService {
    private final ConsentRecordRepository repo; private final PrivacyEventRepository events;
    public ConsentService(ConsentRecordRepository r, PrivacyEventRepository e){ this.repo = r; this.events = e; }


    @Transactional
    public ConsentRecord upsert(ConsentUpsertRequest req){
        ConsentRecord c = new ConsentRecord();
        c.setSubjectId(req.getSubjectId()); c.setSessionId(req.getSessionId()); c.setCategory(req.getCategory()); c.setStatus(req.getStatus()); c.setPolicyVersion(req.getPolicyVersion()); c.setSource(req.getSource());
        ConsentRecord saved = repo.save(c);
        PrivacyEvent e = new PrivacyEvent(); e.setSubjectId(req.getSubjectId()); e.setKind("CONSENT_CHANGED"); e.setPayloadJson("{\"category\":\""+req.getCategory()+"\",\"status\":\""+req.getStatus()+"\"}"); events.save(e);
        return saved;
    }
}