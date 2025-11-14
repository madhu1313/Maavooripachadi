package com.maavooripachadi.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditLogTest {

    @Test
    void storesActorActionDetailsAndIp() {
        AuditLog log = new AuditLog();
        log.setActor("admin");
        log.setAction("LOGIN");
        log.setDetailsJson("{\"success\":true}");
        log.setIp("10.0.0.1");

        assertEquals("admin", log.getActor());
        assertEquals("LOGIN", log.getAction());
        assertEquals("{\"success\":true}", log.getDetailsJson());
        assertEquals("10.0.0.1", log.getIp());
    }
}
