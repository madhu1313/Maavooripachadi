package com.maavooripachadi.security;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserDetailsServiceImplTest {

    @Test
    void classLoads() {
        assertDoesNotThrow(() -> Class.forName("com.maavooripachadi.security.UserDetailsServiceImpl"));
    }
}
