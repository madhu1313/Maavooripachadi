package com.maavooripachadi;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class MaavooriPachadiApplicationTest {

  @Test
  void mainInvokesSpringApplicationRun() {
    String[] args = {"--spring.profiles.active=test"};

    try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
      MaavooriPachadiApplication.main(args);

      mocked.verify(() -> SpringApplication.run(MaavooriPachadiApplication.class, args));
    }
  }
}
