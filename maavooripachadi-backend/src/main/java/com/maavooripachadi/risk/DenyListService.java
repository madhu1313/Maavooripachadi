package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.DenyListUpsertRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class DenyListService {
    private final DenyListRepository repo;
    public DenyListService(DenyListRepository repo){ this.repo = repo; }

    @Transactional
    public DenyListEntry upsert(DenyListUpsertRequest req){
        DenyListEntry e = repo.findByTypeAndValue(req.getType(), req.getValue()).orElse(new DenyListEntry());
        e.setType(req.getType()); e.setValue(req.getValue()); e.setReason(req.getReason()); e.setSource(req.getSource());
        if (req.getExpiresAt()!=null && !req.getExpiresAt().isBlank()) e.setExpiresAt(OffsetDateTime.parse(req.getExpiresAt()));
        return repo.save(e);
    }

    @Transactional(readOnly = true)
    public boolean isDenied(DenyType t, String v){
        return repo.findByTypeAndValue(t, v).map(x -> x.getExpiresAt()==null || x.getExpiresAt().isAfter(OffsetDateTime.now())).orElse(false);
    }
}
