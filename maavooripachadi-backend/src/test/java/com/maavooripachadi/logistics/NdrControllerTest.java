package com.maavooripachadi.logistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class NdrControllerTest {

  private NdrService service;
  private NdrController controller;

  @BeforeEach
  void setUp() {
    service = mock(NdrService.class);
    controller = new NdrController(service);
  }

  @Test
  void issueReturnsSignedUrl() {
    when(service.issue("ORD-1")).thenReturn("token123");

    Map<String, String> response = controller.issue("ORD-1");

    assertThat(response.get("url")).endsWith("token=token123");
    verify(service).issue("ORD-1");
  }

  @Test
  void actionConsumesTokenWhenValid() {
    when(service.valid("token123")).thenReturn(true);

    Map<String, Object> response = controller.action("token123", Map.of("action", "reschedule"));

    assertThat(response).containsEntry("ok", true);
    verify(service).consume("token123");
  }

  @Test
  void actionThrowsGoneWhenTokenInvalid() {
    when(service.valid("bad-token")).thenReturn(false);

    assertThatThrownBy(() -> controller.action("bad-token", Map.of()))
        .isInstanceOf(ResponseStatusException.class)
        .extracting("statusCode")
        .isEqualTo(HttpStatus.GONE);

    verify(service, never()).consume(anyString());
  }
}
