package com.maavooripachadi.security;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "sec_jwt_token", indexes = @Index(name = "ix_token_user", columnList = "user_id"))
public class JwtToken extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    private Boolean revoked = Boolean.FALSE;

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public TokenType getType() { return type; }
    public void setType(TokenType type) { this.type = type; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Boolean getRevoked() { return revoked; }
    public void setRevoked(Boolean revoked) { this.revoked = revoked; }
}
