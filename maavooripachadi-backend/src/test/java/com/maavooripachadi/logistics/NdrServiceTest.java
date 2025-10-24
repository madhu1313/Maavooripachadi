package com.maavooripachadi.logistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NdrServiceTest {

  private NdrTokenRepository repository;
  private NdrService service;

  @BeforeEach
  void setUp() {
    repository = mock(NdrTokenRepository.class);
    service = new NdrService(repository);
  }

  @Test
  void issuePersistsTokenWithExpiry() {
    ArgumentCaptor<NdrToken> tokenCaptor = ArgumentCaptor.forClass(NdrToken.class);
    when(repository.save(any(NdrToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

    String token = service.issue("ORD-1");

    assertThat(token).isNotBlank().hasSize(32);
    verify(repository).save(tokenCaptor.capture());
    NdrToken saved = tokenCaptor.getValue();
    assertThat(saved.getOrderNo()).isEqualTo("ORD-1");
    assertThat(saved.getToken()).isEqualTo(token);
    assertThat(saved.getExpiresAt()).isAfter(OffsetDateTime.now().plusDays(2));
  }

  @Test
  void validReturnsTrueWhenTokenIsActive() {
    NdrToken token = new NdrToken();
    token.setToken("abc");
    token.setExpiresAt(OffsetDateTime.now().plusHours(1));
    token.setUsed(false);
    when(repository.findByToken("abc")).thenReturn(Optional.of(token));

    assertThat(service.valid("abc")).isTrue();
  }

  @Test
  void validReturnsFalseWhenExpiredOrUsed() {
    NdrToken expired = new NdrToken();
    expired.setExpiresAt(OffsetDateTime.now().minusHours(1));
    when(repository.findByToken("expired")).thenReturn(Optional.of(expired));

    assertThat(service.valid("expired")).isFalse();

    NdrToken used = new NdrToken();
    used.setExpiresAt(OffsetDateTime.now().plusHours(1));
    used.setUsed(true);
    when(repository.findByToken("used")).thenReturn(Optional.of(used));

    assertThat(service.valid("used")).isFalse();
  }

  @Test
  void consumeMarksTokenAsUsed() {
    NdrToken token = new NdrToken();
    token.setToken("tok");
    when(repository.findByToken("tok")).thenReturn(Optional.of(token));
    when(repository.save(any(NdrToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.consume("tok");

    assertThat(token.getUsed()).isTrue();
    verify(repository).save(token);
  }
}
