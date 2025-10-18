package com.maavooripachadi.support;

import com.maavooripachadi.support.dto.CannedUpsertRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CannedResponseService {
    private final CannedResponseRepository repo;
    public CannedResponseService(CannedResponseRepository repo){ this.repo = repo; }

    @Transactional
    public CannedResponse upsert(CannedUpsertRequest req){
        CannedResponse c = repo.findByKeyName(req.getKeyName()).orElse(new CannedResponse());
        c.setKeyName(req.getKeyName()); c.setBody(req.getBody()); c.setLocale(req.getLocale()==null?"en-IN":req.getLocale());
        return repo.save(c);
    }
}
