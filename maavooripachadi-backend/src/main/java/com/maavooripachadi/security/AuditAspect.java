package com.maavooripachadi.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AuditAspect {
    private final SecurityAuditLogRepository repo;
    public AuditAspect(SecurityAuditLogRepository repo){ this.repo = repo; }

    @AfterReturning("@annotation(audited)")
    public void log(JoinPoint jp, Audited audited){
        String actor = "system";
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a != null) actor = a.getName();
        AuditLog l = new AuditLog(); l.setActor(actor); l.setAction(audited.action()); l.setDetailsJson("{\"method\":\""+jp.getSignature().toShortString()+"\"}");
        repo.save(l);
    }
}
