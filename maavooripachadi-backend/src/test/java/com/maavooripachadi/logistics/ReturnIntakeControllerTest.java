package com.maavooripachadi.logistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReturnIntakeControllerTest {

  private ReturnIntakeService service;
  private ReturnIntakeController controller;

  @BeforeEach
  void setUp() {
    service = mock(ReturnIntakeService.class);
    controller = new ReturnIntakeController(service);
  }

  @Test
  void intakeEndpointInvokesServiceWithAdminActor() {
    Map<String, Object> response = controller.intake(101L, 7L, "sealed", true, 55L, 3);

    assertThat(response).containsEntry("ok", true);
    verify(service).process(101L, 7L, "sealed", true, 55L, 3, "admin");
  }
}
