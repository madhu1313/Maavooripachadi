package com.maavooripachadi.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AccessDeniedTest {

    @Test
    void sendsForbiddenResponse() throws Exception {
        AccessDenied handler = new AccessDenied();
        HttpServletResponse response = mock(HttpServletResponse.class);

        handler.handle(mock(HttpServletRequest.class), response, mock(AccessDeniedException.class));

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }
}
