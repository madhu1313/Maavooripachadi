package com.maavooripachadi.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @Mock
    private SecurityAuditLogRepository repository;

    private AuditAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new AuditAspect(repository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void logsAuthenticatedUserAction() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("admin@maavooripachadi.com", "pw"));
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("DemoService.doSomething()");
        when(joinPoint.getSignature()).thenReturn(signature);
        Audited audited = audited("TEST_ACTION");

        aspect.log(joinPoint, audited);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(repository).save(captor.capture());
        AuditLog log = captor.getValue();
        assertEquals("admin@maavooripachadi.com", log.getActor());
        assertEquals("TEST_ACTION", log.getAction());
    }

    @Test
    void fallsBackToSystemWhenUnauthenticated() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        Signature signature = mock(Signature.class);
        when(signature.toShortString()).thenReturn("DemoService.doSomething()");
        when(joinPoint.getSignature()).thenReturn(signature);
        Audited audited = audited("TEST_ACTION");

        aspect.log(joinPoint, audited);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(repository).save(captor.capture());
        assertEquals("system", captor.getValue().getActor());
    }
    private static Audited audited(String action) {
        return new Audited() {
            @Override
            public String action() {
                return action;
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Audited.class;
            }
        };
    }
}
