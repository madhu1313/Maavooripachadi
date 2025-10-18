package com.maavooripachadi.security;

import com.maavooripachadi.security.dto.AssignRoleRequest;
import com.maavooripachadi.security.dto.CreateUserRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/security")
public class SecurityAdminController {
    private final AuthService auth; private final UserAdminService admin; private final RoleRepository roles; private final PermissionRepository perms;
    public SecurityAdminController(AuthService auth, UserAdminService admin, RoleRepository roles, PermissionRepository perms){ this.auth=auth; this.admin=admin; this.roles=roles; this.perms=perms; }

    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    @Audited(action = "USER_CREATE")
    public AppUser create(@Valid @RequestBody CreateUserRequest req){ return auth.createUser(req.getEmail(), req.getPassword(), req.getFullName()); }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    @Audited(action = "ROLE_ASSIGN")
    public AppUser assign(@Valid @RequestBody AssignRoleRequest req){ return admin.assignRole(req); }

    @PostMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    public Role saveRole(@RequestBody Role r){ return roles.save(r); }

    @PostMapping("/permission")
    @PreAuthorize("hasRole('ADMIN')")
    public Permission savePerm(@RequestBody Permission p){ return perms.save(p); }
}
