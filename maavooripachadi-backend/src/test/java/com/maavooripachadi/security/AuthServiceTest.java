package com.maavooripachadi.security;

import com.maavooripachadi.security.dto.LoginRequest;
import com.maavooripachadi.security.dto.LoginResponse;
import com.maavooripachadi.security.dto.RegisterRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private JwtTokenRepository tokenRepository;
    @Mock private AppUserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RoleRepository roleRepository;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(
            authenticationManager,
            jwtService,
            tokenRepository,
            userRepository,
            passwordEncoder,
            roleRepository
        );
    }

    @Test
    void loginAuthenticatesUserAndIssuesTokens() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("alice@example.com");
        request.setPassword("secret");

        Role role = new Role();
        role.setName("CUSTOMER");

        AppUser user = new AppUser();
        user.setEmail("alice@example.com");
        user.setFullName("Alice");
        user.getRoles().add(role);

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(jwtService.createToken(eq("alice@example.com"), any(HashMap.class), eq(TokenType.ACCESS))).thenReturn("access-token");
        when(jwtService.createToken(eq("alice@example.com"), any(HashMap.class), eq(TokenType.REFRESH))).thenReturn("refresh-token");
        when(tokenRepository.save(any(JwtToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoginResponse response = service.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getRoles()).containsExactly("CUSTOMER");
    }

    @Test
    void registerCreatesUserWhenIdentifierAvailable() {
        RegisterRequest request = new RegisterRequest();
        request.setIdentifier("bob@example.com");
        request.setPassword("password");
        request.setFullName("Bob");

        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.empty());

        Role customerRole = new Role();
        customerRole.setName("CUSTOMER");
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(customerRole));

        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenRepository.save(any(JwtToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.createToken(eq("bob@example.com"), any(HashMap.class), eq(TokenType.ACCESS))).thenReturn("access");
        when(jwtService.createToken(eq("bob@example.com"), any(HashMap.class), eq(TokenType.REFRESH))).thenReturn("refresh");

        LoginResponse response = service.register(request);

        assertThat(response.getAccessToken()).isEqualTo("access");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(AppUser.class));
    }

    @Test
    void registerFailsWhenIdentifierAlreadyUsed() {
        RegisterRequest request = new RegisterRequest();
        request.setIdentifier("duplicate@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail("duplicate@example.com")).thenReturn(Optional.of(new AppUser()));

        assertThatThrownBy(() -> service.register(request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void refreshValidatesTokenAndReturnsAccessToken() {
        JwtToken refreshRecord = new JwtToken();
        AppUser user = new AppUser();
        user.setEmail("charlie@example.com");
        refreshRecord.setUser(user);
        refreshRecord.setType(TokenType.REFRESH);
        refreshRecord.setToken("refresh-token");
        when(tokenRepository.findByTokenAndType("refresh-token", TokenType.REFRESH)).thenReturn(Optional.of(refreshRecord));

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("charlie@example.com");
        @SuppressWarnings("unchecked")
        Jws<Claims> jws = mock(Jws.class);
        when(jws.getBody()).thenReturn(claims);
        when(jwtService.parse("refresh-token")).thenReturn(jws);
        when(jwtService.createToken(eq("charlie@example.com"), any(HashMap.class), eq(TokenType.ACCESS))).thenReturn("new-access");

        String accessToken = service.refresh("refresh-token");

        assertThat(accessToken).isEqualTo("new-access");
    }

    @Test
    void logoutMarksRefreshTokenRevoked() {
        JwtToken token = new JwtToken();
        when(tokenRepository.findByTokenAndType("refresh-token", TokenType.REFRESH)).thenReturn(Optional.of(token));
        when(tokenRepository.save(token)).thenReturn(token);

        service.logout("refresh-token");

        assertThat(token.getRevoked()).isTrue();
    }
}
