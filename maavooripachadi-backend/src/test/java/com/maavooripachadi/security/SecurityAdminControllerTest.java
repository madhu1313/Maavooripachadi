package com.maavooripachadi.security;

import com.maavooripachadi.security.dto.AssignRoleRequest;
import com.maavooripachadi.security.dto.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecurityAdminControllerTest {

    private AuthService authService;
    private UserAdminService userAdminService;
    private RoleRepository roleRepository;
    private PermissionRepository permissionRepository;
    private SecurityAdminController controller;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        userAdminService = mock(UserAdminService.class);
        roleRepository = mock(RoleRepository.class);
        permissionRepository = mock(PermissionRepository.class);
        controller = new SecurityAdminController(authService, userAdminService, roleRepository, permissionRepository);
    }

    @Test
    void createDelegatesToAuthService() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("admin@example.com");
        request.setPassword("password");
        request.setFullName("Admin");

        AppUser user = new AppUser();
        when(authService.createUser("admin@example.com", "password", "Admin")).thenReturn(user);

        assertThat(controller.create(request)).isSameAs(user);
    }

    @Test
    void assignRoleDelegatesToUserAdminService() {
        AssignRoleRequest request = new AssignRoleRequest();
        AppUser user = new AppUser();
        when(userAdminService.assignRole(request)).thenReturn(user);

        assertThat(controller.assign(request)).isSameAs(user);
    }

    @Test
    void saveRoleDelegatesToRepository() {
        Role role = new Role();
        when(roleRepository.save(role)).thenReturn(role);

        assertThat(controller.saveRole(role)).isSameAs(role);
    }

    @Test
    void savePermissionDelegatesToRepository() {
        Permission permission = new Permission();
        when(permissionRepository.save(permission)).thenReturn(permission);

        assertThat(controller.savePerm(permission)).isSameAs(permission);
    }
}
