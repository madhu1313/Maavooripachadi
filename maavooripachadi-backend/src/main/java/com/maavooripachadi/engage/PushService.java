package com.maavooripachadi.engage;


import com.maavooripachadi.engage.dto.RegisterPushTokenRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.List;


@Service
public class PushService {
    private final PushTokenRepository repo;


    public PushService(PushTokenRepository repo){ this.repo = repo; }


    @Transactional
    public PushToken register(RegisterPushTokenRequest req){
        var existing = repo.findByToken(req.token()).orElse(null);
        PushToken t = existing == null ? new PushToken() : existing;
        t.setDeviceId(req.deviceId());
        t.setToken(req.token());
        t.setPlatform(req.platform());
        t.setUserId(req.userId());
        t.setEnabled(Boolean.TRUE);
        t.setLastSeenAt(OffsetDateTime.now());
        return repo.save(t);
    }


    @Transactional
    public void disableByToken(String token){
        var t = repo.findByToken(token).orElseThrow();
        t.setEnabled(Boolean.FALSE);
        repo.save(t);
    }


    @Transactional(readOnly = true)
    public List<PushToken> list(){ return repo.findAll(); }
}