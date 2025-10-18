package com.maavooripachadi.privacy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyService {
    private final PrivacyPolicyRepository repo;
    private final PrivacyEventRepository events;

    public PolicyService(PrivacyPolicyRepository repo, PrivacyEventRepository events){ this.repo = repo; this.events = events; }

    @Transactional(readOnly = true)
    public PrivacyPolicy getActive(){ return repo.findFirstByActiveTrueOrderByCreatedAtDesc().orElse(null); }

    @Transactional
    public PrivacyPolicy publish(PrivacyPolicy draft){
        // deactivate others if this is activated
        if (Boolean.TRUE.equals(draft.getActive())){
            repo.findAll().forEach(p -> { if (Boolean.TRUE.equals(p.getActive())){ p.setActive(Boolean.FALSE); repo.save(p); } });
        }
        PrivacyPolicy saved = repo.save(draft);
        PrivacyEvent e = new PrivacyEvent(); e.setKind("POLICY_PUBLISHED"); e.setPayloadJson("{\"version\":\"" + saved.getPolicyVersion() + "\"}"); events.save(e);
        return saved;
    }
}
