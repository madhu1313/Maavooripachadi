package com.maavooripachadi.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginIdentifierTest {

    @Test
    void parsesEmailIdentifiers() {
        LoginIdentifier.Parsed parsed = LoginIdentifier.parse("User@Example.com");
        assertEquals(LoginIdentifier.Type.EMAIL, parsed.type());
        assertEquals("user@example.com", parsed.value());
    }

    @Test
    void parsesPhoneIdentifiers() {
        LoginIdentifier.Parsed parsed = LoginIdentifier.parse("+91 99999-88888");
        assertEquals(LoginIdentifier.Type.PHONE, parsed.type());
        assertEquals("919999988888", parsed.value());
    }

    @Test
    void rejectsInvalidIdentifier() {
        assertThrows(IllegalArgumentException.class, () -> LoginIdentifier.parse("not valid"));
    }

    @Test
    void normalizePhoneHandlesInvalidValues() {
        assertNull(LoginIdentifier.normalizePhone("123")); // too short
        assertEquals("1234567890", LoginIdentifier.normalizePhone("(123) 456-7890"));
    }
}
