package com.maavooripachadi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final UserDetailsService uds; private final JwtTokenRepository tokens;
    public JwtAuthFilter(JwtService jwt, UserDetailsService uds, JwtTokenRepository tokens){ this.jwt = jwt; this.uds = uds; this.tokens = tokens; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String hdr = req.getHeader("Authorization");
        if (StringUtils.hasText(hdr) && hdr.startsWith("Bearer ")){
            String token = hdr.substring(7);
            try {
                var jws = jwt.parse(token);
                String username = jws.getBody().getSubject();
                var rec = tokens.findByTokenAndType(token, TokenType.ACCESS);
                if (rec.isPresent() && !Boolean.TRUE.equals(rec.get().getRevoked())){
                    UserDetails ud = uds.loadUserByUsername(username);
                    var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) { }
        }
        chain.doFilter(req, res);
    }
}
