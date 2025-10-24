package com.maavooripachadi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitServiceTest {

    private RateLimitService service;
    private SecurityProperties props;

    @BeforeEach
    void setUp() {
        props = new SecurityProperties();
        props.setRateWindowSeconds(1);
        props.setRateMaxCalls(2);
        service = new RateLimitService(props);
    }

    @Test
    void allowRespectsWindowAndLimit() {
        assertThat(service.allow("client")).isTrue();
        assertThat(service.allow("client")).isTrue();
        assertThat(service.allow("client")).isFalse();
    }
}
