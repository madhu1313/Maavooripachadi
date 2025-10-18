package com.maavooripachadi.privacy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PrivacyExportServiceTest {

    @Test
    void classLoads() {
        assertDoesNotThrow(() -> Class.forName("com.maavooripachadi.privacy.PrivacyExportService"));
    }
}
