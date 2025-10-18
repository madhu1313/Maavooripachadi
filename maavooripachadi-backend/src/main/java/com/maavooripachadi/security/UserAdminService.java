package com.maavooripachadi.security;

import com.maavooripachadi.security.dto.AssignRoleRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserAdminService {
    private final AppUserRepository users; private final RoleRepository roles;
    public UserAdminService(AppUserRepository users, RoleRepository roles){ this.users = users; this.roles = roles; }

    @Transactional
    public AppUser assignRole(AssignRoleRequest req){
        LoginIdentifier.Parsed identifier;
        try {
            identifier = LoginIdentifier.parse(req.getIdentifier());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }

        AppUser u = (switch (identifier.type()) {
            case EMAIL -> users.findByEmail(identifier.value());
            case PHONE -> users.findByPhone(identifier.value());
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        Role r = roles.findByName(req.getRole()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found."));
        u.getRoles().add(r); return users.save(u);
    }
}
