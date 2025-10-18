package com.maavooripachadi.security;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByTokenAndType(String token, TokenType type);
}
