package com.maavooripachadi.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserMfaFieldsTest {

    @Test
    void storesMfaAttributes() {
        UserMfaFields fields = new UserMfaFields();
        fields.setUserId(101L);
        fields.setMfaSecret("secret");
        fields.setMfaEnabled(true);

        assertEquals(101L, fields.getUserId());
        assertEquals("secret", fields.getMfaSecret());
        assertEquals(true, fields.getMfaEnabled());
    }

    @Test
    void defaultIsDisabled() {
        UserMfaFields fields = new UserMfaFields();
        assertFalse(fields.getMfaEnabled());
    }
}
