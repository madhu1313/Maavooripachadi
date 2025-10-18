package com.maavooripachadi.support;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SupportControllerTest {

    @Test
    void classLoads() {
        assertDoesNotThrow(() -> Class.forName("com.maavooripachadi.support.SupportPublicController"));
    }
}
