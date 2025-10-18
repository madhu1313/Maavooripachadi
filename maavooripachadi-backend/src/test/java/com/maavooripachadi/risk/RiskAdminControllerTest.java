package com.maavooripachadi.risk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RiskAdminControllerTest {

    @Test
    void classLoads() {
        assertDoesNotThrow(() -> Class.forName("com.maavooripachadi.risk.RiskAdminController"));
    }
}
