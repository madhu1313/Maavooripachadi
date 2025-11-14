package com.maavooripachadi.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthEntryPointTest {

    @Test
    void respondsWithUnauthorized() throws Exception {
        AuthEntryPoint entryPoint = new AuthEntryPoint();
        HttpServletResponse response = mock(HttpServletResponse.class);

        entryPoint.commence(mock(HttpServletRequest.class), response, mock(AuthenticationException.class));

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
