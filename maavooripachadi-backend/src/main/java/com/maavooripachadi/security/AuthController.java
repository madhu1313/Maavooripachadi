package com.maavooripachadi.security;

import com.maavooripachadi.security.dto.LoginRequest;
import com.maavooripachadi.security.dto.LoginResponse;
import com.maavooripachadi.security.dto.RefreshRequest;
import com.maavooripachadi.security.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService auth;
    private final RateLimitService rate;
    private final AppUserRepository users;

    public AuthController(AuthService auth, RateLimitService rate, AppUserRepository users){ this.auth = auth; this.rate = rate; this.users = users; }

    @PostMapping("/login")
    @Audited(action = "LOGIN")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req){
        String fingerprint = normaliseIdentifier(req.getIdentifier());
        if (!rate.allow("login:"+fingerprint)) return ResponseEntity.status(429).build();
        return ResponseEntity.ok(auth.login(req));
    }

    @PostMapping("/register")
    @Audited(action = "REGISTER")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest req){
        String fingerprint = normaliseIdentifier(req.getIdentifier());
        if (!rate.allow("register:"+fingerprint)) return ResponseEntity.status(429).build();
        return ResponseEntity.ok(auth.register(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<java.util.Map<String,String>> refresh(@Valid @RequestBody RefreshRequest req){
        String access = auth.refresh(req.getRefreshToken());
        return ResponseEntity.ok(java.util.Map.of("accessToken", access, "tokenType", "Bearer"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest req){ auth.logout(req.getRefreshToken()); return ResponseEntity.ok().build(); }

    @GetMapping("/me")
    public ResponseEntity<java.util.Map<String,Object>> me(@AuthenticationPrincipal UserDetails ud){
        if (ud == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        String username = ud.getUsername();
        LoginIdentifier.Parsed parsed;
        try {
            parsed = LoginIdentifier.parse(username);
        } catch (IllegalArgumentException ex) {
            parsed = new LoginIdentifier.Parsed(LoginIdentifier.Type.EMAIL, username);
        }

        java.util.Optional<AppUser> userOpt = switch (parsed.type()) {
            case EMAIL -> users.findByEmail(parsed.value());
            case PHONE -> users.findByPhone(parsed.value());
        };

        java.util.List<String> authorities = ud.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList();

        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(java.util.Map.of(
                    "identifier", username,
                    "email", username,
                    "roles", authorities,
                    "authorities", authorities
            ));
        }

        AppUser user = userOpt.get();
        java.util.List<String> roles = user.getRoles().stream().map(Role::getName).sorted().toList();

        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("identifier", user.getLoginIdentifier());
        payload.put("email", user.getEmail());
        payload.put("phone", user.getPhone());
        payload.put("name", user.getFullName());
        payload.put("roles", roles);
        payload.put("authorities", authorities);
        return ResponseEntity.ok(payload);
    }

    private String normaliseIdentifier(String raw){
        if (raw == null) return "";
        try {
            return LoginIdentifier.parse(raw).value();
        } catch (IllegalArgumentException ex){
            return raw.trim().toLowerCase();
        }
    }
}
