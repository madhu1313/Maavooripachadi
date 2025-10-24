package com.maavooripachadi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock private AppUserRepository userRepository;
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByEmailReturnsAuthorities() {
        AppUser user = new AppUser();
        user.setEmail("alice@example.com");
        user.setPasswordHash("hash");

        Role role = new Role();
        role.setName("ADMIN");
        Permission permission = new Permission();
        permission.setName("PRICING_WRITE");
        role.getPermissions().add(permission);
        user.getRoles().add(role);

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("alice@example.com");

        assertThat(details.getUsername()).isEqualTo("alice@example.com");
        assertThat(details.getAuthorities()).extracting("authority")
                .contains("ROLE_ADMIN", "PRICING_WRITE");
    }

    @Test
    void loadUserByPhoneLooksUpPhone() {
        AppUser user = new AppUser();
        user.setPhone("9999999999");
        user.setPasswordHash("hash");
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("+91 99999 99999");

        assertThat(details.getUsername()).isEqualTo("9999999999");
    }

    @Test
    void loadUserThrowsWhenNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("missing@example.com"))
            .isInstanceOf(UsernameNotFoundException.class);
    }
}
