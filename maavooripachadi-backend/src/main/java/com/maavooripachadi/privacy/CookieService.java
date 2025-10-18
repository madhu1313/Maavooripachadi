package com.maavooripachadi.privacy;


import com.maavooripachadi.privacy.dto.CookiePrefRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CookieService {
    private final CookiePreferenceRepository repo;
    public CookieService(CookiePreferenceRepository repo){ this.repo = repo; }


    @Transactional
    public CookiePreference set(CookiePrefRequest req){
        CookiePreference cp = repo.findFirstBySubjectIdOrSessionIdOrderByCreatedAtDesc(req.getSubjectId(), req.getSessionId()).orElse(new CookiePreference());
        cp.setSubjectId(req.getSubjectId()); cp.setSessionId(req.getSessionId());
        if (req.getAnalytics()!=null) cp.setAnalytics(req.getAnalytics());
        if (req.getMarketing()!=null) cp.setMarketing(req.getMarketing());
        if (req.getPersonalization()!=null) cp.setPersonalization(req.getPersonalization());
        return repo.save(cp);
    }
}