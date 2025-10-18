package com.maavooripachadi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final SecurityProperties props;
    private final Key key;

    public JwtService(SecurityProperties props){
        this.props = props;
        byte[] rawSecret = props.getJwtSecret().getBytes();
        if (rawSecret.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(rawSecret, 0, padded, 0, rawSecret.length);
            for (int i = rawSecret.length; i < 32; i++) {
                padded[i] = (byte) (rawSecret[i % rawSecret.length] ^ (i * 31));
            }
            rawSecret = padded;
        }
        this.key = Keys.hmacShaKeyFor(rawSecret);
    }

    public String createToken(String subject, Map<String, Object> claims, TokenType type){
        long ttl = type == TokenType.ACCESS ? props.getAccessTtlSeconds() : props.getRefreshTtlSeconds();
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttl)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token){ return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); }
}
