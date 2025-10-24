package com.maavooripachadi.security;

import com.maavooripachadi.security.dto.LoginRequest;
import com.maavooripachadi.security.dto.LoginResponse;
import com.maavooripachadi.security.dto.RefreshRequest;
import com.maavooripachadi.security.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private RateLimitService rateLimitService;
    private AppUserRepository userRepository;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        rateLimitService = mock(RateLimitService.class);
        userRepository = mock(AppUserRepository.class);
        controller = new AuthController(authService, rateLimitService, userRepository);
    }

    @Test
    void loginReturnsTokensWhenAllowed() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("alice@example.com");
        request.setPassword("password");

        LoginResponse response = new LoginResponse();
        response.setAccessToken("token");
        when(rateLimitService.allow("login:alice@example.com")).thenReturn(true);
        when(authService.login(request)).thenReturn(response);

        ResponseEntity<LoginResponse> entity = controller.login(request);

        assertThat(entity.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(entity.getBody()).isSameAs(response);
    }

    @Test
    void loginReturns429WhenRateLimited() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("blocked@example.com");
        when(rateLimitService.allow("login:blocked@example.com")).thenReturn(false);

        ResponseEntity<LoginResponse> entity = controller.login(request);

        assertThat(entity.getStatusCode().value()).isEqualTo(429);
        verify(authService, never()).login(any());
    }

    @Test
    void registerDelegatesWhenAllowed() {
        RegisterRequest request = new RegisterRequest();
        request.setIdentifier("new@example.com");
        request.setPassword("password");
        request.setFullName("New User");

        LoginResponse response = new LoginResponse();
        when(rateLimitService.allow("register:new@example.com")).thenReturn(true);
        when(authService.register(request)).thenReturn(response);

        ResponseEntity<LoginResponse> entity = controller.register(request);

        assertThat(entity.getBody()).isSameAs(response);
    }

    @Test
    void refreshReturnsAccessToken() {
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("refresh-token");
        when(authService.refresh("refresh-token")).thenReturn("access-token");

        ResponseEntity<java.util.Map<String, String>> entity = controller.refresh(request);

        assertThat(entity.getBody()).containsEntry("accessToken", "access-token");
    }

    @Test
    void meReturnsCurrentUserDetails() {
        AppUser user = new AppUser();
        user.setEmail("me@example.com");
        user.setPhone("9999999999");
        user.setFullName("Current User");

        Role role = new Role();
        role.setName("CUSTOMER");
        user.getRoles().add(role);

        when(userRepository.findByEmail("me@example.com")).thenReturn(Optional.of(user));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        User principal = new User("me@example.com", "pw", authorities);

        ResponseEntity<java.util.Map<String, Object>> entity = controller.me(principal);
        java.util.Map<String, Object> body = entity.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("email")).isEqualTo("me@example.com");
        assertThat(body.get("name")).isEqualTo("Current User");
        assertThat(body.get("roles")).isEqualTo(List.of("CUSTOMER"));
        assertThat(body.get("authorities")).isEqualTo(List.of("ROLE_CUSTOMER"));
    }

    @Test
    void meReturnsUnauthorizedWhenPrincipalMissing() {
        ResponseEntity<java.util.Map<String, Object>> entity = controller.me(null);
        assertThat(entity.getStatusCode().value()).isEqualTo(401);
    }
}
