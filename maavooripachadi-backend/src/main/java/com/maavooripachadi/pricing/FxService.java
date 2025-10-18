package com.maavooripachadi.pricing;


import com.maavooripachadi.pricing.dto.SetRateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Service
public class FxService {
    private final CurrencyRateRepository repo;
    public FxService(CurrencyRateRepository repo){ this.repo = repo; }


    @Transactional
    public CurrencyRate setRate(SetRateRequest req){
        var r = repo.findByFromAndTo(req.getFrom().toUpperCase(), req.getTo().toUpperCase()).orElseGet(CurrencyRate::new);
        r.setFrom(req.getFrom().toUpperCase()); r.setTo(req.getTo().toUpperCase()); r.setRate(req.getRate()); r.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return repo.save(r);
    }
}