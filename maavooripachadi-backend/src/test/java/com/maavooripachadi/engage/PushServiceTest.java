package com.maavooripachadi.engage;

import com.maavooripachadi.engage.dto.RegisterPushTokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PushServiceTest {

  private PushTokenRepository repository;
  private PushService service;

  @BeforeEach
  void setUp() {
    repository = mock(PushTokenRepository.class);
    service = new PushService(repository);
  }

  @Test
  void registerCreatesNewTokenWhenMissing() {
    RegisterPushTokenRequest request = new RegisterPushTokenRequest("device-1", "token-1", "ANDROID", "user-1");

    when(repository.findByToken("token-1")).thenReturn(Optional.empty());
    when(repository.save(any(PushToken.class))).thenAnswer(invocation -> {
      PushToken token = invocation.getArgument(0);
      ReflectionTestUtils.setField(token, "id", 10L);
      return token;
    });

    PushToken saved = service.register(request);

    assertThat(saved.getDeviceId()).isEqualTo("device-1");
    assertThat(saved.getToken()).isEqualTo("token-1");
    assertThat(saved.getPlatform()).isEqualTo("ANDROID");
    assertThat(saved.getUserId()).isEqualTo("user-1");
    assertThat(saved.getEnabled()).isTrue();
    assertThat(saved.getLastSeenAt()).isNotNull();
    verify(repository).save(saved);
  }

  @Test
  void registerUpdatesExistingToken() {
    PushToken existing = new PushToken();
    existing.setDeviceId("device-0");
    existing.setToken("token-1");
    existing.setPlatform("ANDROID");
    existing.setEnabled(false);

    when(repository.findByToken("token-1")).thenReturn(Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    RegisterPushTokenRequest request = new RegisterPushTokenRequest("device-1", "token-1", "IOS", "user-2");
    PushToken saved = service.register(request);

    assertThat(saved).isSameAs(existing);
    assertThat(saved.getDeviceId()).isEqualTo("device-1");
    assertThat(saved.getPlatform()).isEqualTo("IOS");
    assertThat(saved.getUserId()).isEqualTo("user-2");
    assertThat(saved.getEnabled()).isTrue();
    assertThat(saved.getLastSeenAt()).isNotNull();
    verify(repository).save(existing);
  }

  @Test
  void disableByTokenMarksTokenDisabled() {
    PushToken token = new PushToken();
    token.setToken("token-1");
    token.setEnabled(true);

    when(repository.findByToken("token-1")).thenReturn(Optional.of(token));

    service.disableByToken("token-1");

    assertThat(token.getEnabled()).isFalse();
    verify(repository).save(token);
  }

  @Test
  void listReturnsAllTokens() {
    List<PushToken> tokens = List.of(new PushToken(), new PushToken());
    when(repository.findAll()).thenReturn(tokens);

    assertThat(service.list()).isEqualTo(tokens);
  }
}
