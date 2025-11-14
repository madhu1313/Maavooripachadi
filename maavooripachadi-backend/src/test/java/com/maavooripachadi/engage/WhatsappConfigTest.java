package com.maavooripachadi.engage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WhatsappConfigTest {

    @Test
    void configurationInstantiates() {
        WhatsappConfig config = new WhatsappConfig();
        assertNotNull(config);
    }
}
