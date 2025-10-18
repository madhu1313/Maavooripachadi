package com.maavooripachadi.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository users;
    public UserDetailsServiceImpl(AppUserRepository users){ this.users = users; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginIdentifier.Parsed identifier;
        try {
            identifier = LoginIdentifier.parse(username);
        } catch (IllegalArgumentException ex) {
            identifier = new LoginIdentifier.Parsed(LoginIdentifier.Type.EMAIL, username.trim().toLowerCase());
        }

        AppUser u = switch (identifier.type()) {
            case EMAIL -> users.findByEmail(identifier.value()).orElseThrow(() -> new UsernameNotFoundException("Not found"));
            case PHONE -> users.findByPhone(identifier.value()).orElseThrow(() -> new UsernameNotFoundException("Not found"));
        };
        Set<GrantedAuthority> auths = new HashSet<>();
        for (Role r : u.getRoles()){
            auths.add(new SimpleGrantedAuthority("ROLE_"+r.getName()));
            for (Permission p : r.getPermissions()) auths.add(new SimpleGrantedAuthority(p.getName()));
        }
        String principal = u.getLoginIdentifier();
        return new User(principal, u.getPasswordHash(), Boolean.TRUE.equals(u.getEnabled()), true, true, true, auths);
    }
}
