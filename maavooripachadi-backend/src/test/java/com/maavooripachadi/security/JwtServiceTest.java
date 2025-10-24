package com.maavooripachadi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private SecurityProperties properties;

    @BeforeEach
    void setUp() {
        properties = new SecurityProperties();
        properties.setJwtSecret("super-secret-signing-key-that-is-long-enough");
        properties.setAccessTtlSeconds(60);
        jwtService = new JwtService(properties);
    }

    @Test
    void createTokenAndParse() {
        String token = jwtService.createToken("subject@example.com", java.util.Map.of("foo", "bar"), TokenType.ACCESS);

        Jws<Claims> parsed = jwtService.parse(token);
        assertThat(parsed.getBody().getSubject()).isEqualTo("subject@example.com");
        assertThat(parsed.getBody().get("foo")).isEqualTo("bar");

        Instant issuedAt = parsed.getBody().getIssuedAt().toInstant();
        Instant expiresAt = parsed.getBody().getExpiration().toInstant();
        assertThat(expiresAt).isAfter(issuedAt);
    }
}
