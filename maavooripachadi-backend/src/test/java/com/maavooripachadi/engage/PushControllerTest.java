package com.maavooripachadi.engage;

import com.maavooripachadi.engage.dto.RegisterPushTokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PushControllerTest {

  private PushService service;
  private PushController controller;

  @BeforeEach
  void setUp() {
    service = mock(PushService.class);
    controller = new PushController(service);
  }

  @Test
  void registerEndpointDelegatesToService() {
    RegisterPushTokenRequest request = new RegisterPushTokenRequest("device", "token", "ANDROID", "user");
    PushToken token = new PushToken();
    when(service.register(request)).thenReturn(token);

    PushToken response = controller.register(request);

    assertThat(response).isSameAs(token);
    verify(service).register(request);
  }

  @Test
  void disableEndpointDelegatesToService() {
    var response = controller.disable("token");

    assertThat(response).containsEntry("ok", true);
    verify(service).disableByToken("token");
  }

  @Test
  void listEndpointReturnsTokens() {
    List<PushToken> tokens = List.of(new PushToken());
    when(service.list()).thenReturn(tokens);

    assertThat(controller.list()).isSameAs(tokens);
  }
}
