package com.maavooripachadi.admin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AdminControllerTest {

    @Test
    void classLoads() {
        assertDoesNotThrow(() -> Class.forName("com.maavooripachadi.admin.AdminController"));
    }
}
