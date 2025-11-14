package com.maavooripachadi.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SupportConfigTest {

    @Test
    void canInstantiateConfiguration() {
        SupportConfig config = new SupportConfig();
        assertNotNull(config);
    }
}
