package com.maavooripachadi.security.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssignRoleRequestTest {

    @Test
    void storesIdentifierAndRole() {
        AssignRoleRequest request = new AssignRoleRequest();
        request.setIdentifier("admin@maavooripachadi.com");
        request.setRole("ADMIN");

        assertEquals("admin@maavooripachadi.com", request.getIdentifier());
        assertEquals("ADMIN", request.getRole());
    }
}
