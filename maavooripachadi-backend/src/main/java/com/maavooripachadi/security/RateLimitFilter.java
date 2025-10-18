package com.maavooripachadi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService service;
    public RateLimitFilter(RateLimitService service){ this.service = service; }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String key = (req.getUserPrincipal()!=null? req.getUserPrincipal().getName() : "anon:") + (req.getRemoteAddr());
        if (!service.allow(key)){
            res.setStatus(429); res.getWriter().write("Too Many Requests"); return;
        }
        chain.doFilter(req, res);
    }
}
