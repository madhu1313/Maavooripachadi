package com.maavooripachadi.engage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhatsappPropertiesTest {

    @Test
    void hasDefaultsAndAllowsOverrides() {
        WhatsappProperties props = new WhatsappProperties();
        assertEquals("https://graph.facebook.com/v18.0", props.getApiBase());
        assertEquals("91", props.getDefaultCountryCode());
        assertEquals(false, props.isEnabled());

        props.setEnabled(true);
        props.setPhoneNumberId("123");
        props.setAccessToken("token");
        props.setApiBase("https://example.com");
        props.setDefaultCountryCode("1");

        assertEquals(true, props.isEnabled());
        assertEquals("123", props.getPhoneNumberId());
        assertEquals("token", props.getAccessToken());
        assertEquals("https://example.com", props.getApiBase());
        assertEquals("1", props.getDefaultCountryCode());
    }
}
