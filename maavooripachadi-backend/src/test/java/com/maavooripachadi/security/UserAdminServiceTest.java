package com.maavooripachadi.security;

import com.maavooripachadi.security.dto.AssignRoleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private AppUserRepository users;
    @Mock
    private RoleRepository roles;

    @InjectMocks
    private UserAdminService service;

    private AppUser existingUser;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        existingUser = new AppUser();
        existingUser.setEmail("admin@maavooripachadi.com");
        adminRole = new Role();
        adminRole.setName("ADMIN");
    }

    @Test
    void assignRoleAddsRoleForEmailIdentifier() {
        AssignRoleRequest request = new AssignRoleRequest();
        request.setIdentifier("admin@maavooripachadi.com");
        request.setRole("ADMIN");

        when(users.findByEmail("admin@maavooripachadi.com")).thenReturn(Optional.of(existingUser));
        when(roles.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(users.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser saved = service.assignRole(request);

        assertEquals(1, saved.getRoles().size());
        assertEquals(adminRole, saved.getRoles().iterator().next());
        verify(users).save(saved);
    }

    @Test
    void assignRoleFindsUserByPhone() {
        AssignRoleRequest request = new AssignRoleRequest();
        request.setIdentifier("+91 99999 88888");
        request.setRole("ADMIN");

        when(users.findByPhone("919999988888")).thenReturn(Optional.of(existingUser));
        when(roles.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(users.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.assignRole(request);

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(users).save(captor.capture());
        assertEquals(adminRole, captor.getValue().getRoles().iterator().next());
    }

    @Test
    void assignRoleThrowsWhenUserMissing() {
        AssignRoleRequest request = new AssignRoleRequest();
        request.setIdentifier("missing@maavooripachadi.com");
        request.setRole("ADMIN");

        when(users.findByEmail("missing@maavooripachadi.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.assignRole(request));
    }

    @Test
    void assignRoleThrowsForInvalidIdentifier() {
        AssignRoleRequest request = new AssignRoleRequest();
        request.setIdentifier("invalid-id");
        request.setRole("ADMIN");

        assertThrows(ResponseStatusException.class, () -> service.assignRole(request));
    }
}
