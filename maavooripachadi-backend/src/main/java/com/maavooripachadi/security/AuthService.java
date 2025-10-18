package com.maavooripachadi.security;

import com.maavooripachadi.security.dto.LoginRequest;
import com.maavooripachadi.security.dto.LoginResponse;
import com.maavooripachadi.security.dto.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;

@Service
public class AuthService {
    private final AuthenticationManager am;
    private final JwtService jwt;
    private final JwtTokenRepository tokens;
    private final AppUserRepository users;
    private final PasswordEncoder enc;
    private final RoleRepository roles;

    public AuthService(AuthenticationManager am,
                       JwtService jwt,
                       JwtTokenRepository tokens,
                       AppUserRepository users,
                       PasswordEncoder enc,
                       RoleRepository roles) {
        this.am = am;
        this.jwt = jwt;
        this.tokens = tokens;
        this.users = users;
        this.enc = enc;
        this.roles = roles;
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        LoginIdentifier.Parsed identifier = parseIdentifier(req.getIdentifier(), HttpStatus.BAD_REQUEST);
        am.authenticate(new UsernamePasswordAuthenticationToken(identifier.value(), req.getPassword()));
        AppUser user = resolveUser(identifier);
        return issueTokens(user);
    }

    @Transactional
    public LoginResponse register(RegisterRequest req) {
        LoginIdentifier.Parsed identifier = parseIdentifier(req.getIdentifier(), HttpStatus.BAD_REQUEST);
        ensureIdentifierAvailable(identifier);

        AppUser user = new AppUser();
        if (identifier.type() == LoginIdentifier.Type.EMAIL) {
            user.setEmail(identifier.value());
        } else {
            user.setPhone(identifier.value());
        }
        user.setPasswordHash(enc.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setEnabled(Boolean.TRUE);
        user.getRoles().add(resolveCustomerRole());
        users.save(user);

        return issueTokens(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        tokens.findByTokenAndType(refreshToken, TokenType.REFRESH)
                .ifPresent(t -> {
                    t.setRevoked(Boolean.TRUE);
                    tokens.save(t);
                });
    }

    @Transactional
    public String refresh(String refreshToken) {
        var jws = jwt.parse(refreshToken); // throws if bad/expired
        var rec = tokens.findByTokenAndType(refreshToken, TokenType.REFRESH).orElseThrow();
        if (Boolean.TRUE.equals(rec.getRevoked())) {
            throw new RuntimeException("revoked");
        }
        String principal = jws.getBody().getSubject();
        AppUser user = rec.getUser();
        String subject = user != null && user.getLoginIdentifier() != null ? user.getLoginIdentifier() : principal;
        return jwt.createToken(subject, new HashMap<>(), TokenType.ACCESS);
    }

    /* Administrative helper retained for seeding via admin APIs */
    @Transactional
    public AppUser createUser(String email, String rawPassword, String fullName) {
        LoginIdentifier.Parsed identifier = parseIdentifier(email, HttpStatus.BAD_REQUEST);
        if (identifier.type() != LoginIdentifier.Type.EMAIL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Administrative users must have a valid email address.");
        }
        if (users.findByEmail(identifier.value()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists: " + identifier.value());
        }
        AppUser user = new AppUser();
        user.setEmail(identifier.value());
        user.setPasswordHash(enc.encode(rawPassword));
        user.setFullName(fullName);
        user.setEnabled(Boolean.TRUE);
        user.getRoles().add(resolveCustomerRole());
        return users.save(user);
    }

    private LoginResponse issueTokens(AppUser user) {
        String subject = user.getLoginIdentifier();
        if (subject == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User record missing login identifier.");
        }

        String access = jwt.createToken(subject, new HashMap<>(), TokenType.ACCESS);
        String refresh = jwt.createToken(subject, new HashMap<>(), TokenType.REFRESH);

        JwtToken accessRecord = new JwtToken();
        accessRecord.setUser(user);
        accessRecord.setType(TokenType.ACCESS);
        accessRecord.setToken(access);
        tokens.save(accessRecord);

        JwtToken refreshRecord = new JwtToken();
        refreshRecord.setUser(user);
        refreshRecord.setType(TokenType.REFRESH);
        refreshRecord.setToken(refresh);
        tokens.save(refreshRecord);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(access);
        response.setRefreshToken(refresh);
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .sorted()
                .toList();
        response.setRoles(roleNames);
        return response;
    }

    private Role resolveCustomerRole() {
        return roles.findByName("CUSTOMER").orElseGet(() -> {
            Role r = new Role();
            r.setName("CUSTOMER");
            r.setDescription("Default Maavoori customer");
            return roles.save(r);
        });
    }

    private LoginIdentifier.Parsed parseIdentifier(String raw, HttpStatus statusIfInvalid) {
        try {
            return LoginIdentifier.parse(raw);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(statusIfInvalid, ex.getMessage());
        }
    }

    private void ensureIdentifierAvailable(LoginIdentifier.Parsed identifier) {
        boolean exists = switch (identifier.type()) {
            case EMAIL -> users.findByEmail(identifier.value()).isPresent();
            case PHONE -> users.findByPhone(identifier.value()).isPresent();
        };
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists for that email or mobile number.");
        }
    }

    private AppUser resolveUser(LoginIdentifier.Parsed identifier) {
        return (switch (identifier.type()) {
            case EMAIL -> users.findByEmail(identifier.value());
            case PHONE -> users.findByPhone(identifier.value());
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login credentials."));
    }
}
