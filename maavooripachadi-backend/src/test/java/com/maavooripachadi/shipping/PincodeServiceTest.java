package com.maavooripachadi.shipping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PincodeServiceTest {

    private PincodeService service;

    @BeforeEach
    void setUp() {
        service = new PincodeService();
    }

    @Test
    void serviceableReturnsTrueForSixDigitCode() {
        assertThat(service.serviceable("500032")).isTrue();
    }

    @Test
    void serviceableRejectsNullOrIncorrectCodes() {
        assertThat(service.serviceable(null)).isFalse();
        assertThat(service.serviceable("ABCDE")).isFalse();
        assertThat(service.serviceable("12345")).isFalse();
        assertThat(service.serviceable("1234567")).isFalse();
    }
}
