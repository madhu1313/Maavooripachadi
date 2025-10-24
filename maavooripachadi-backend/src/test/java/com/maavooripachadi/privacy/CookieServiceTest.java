package com.maavooripachadi.privacy;

import com.maavooripachadi.privacy.dto.CookiePrefRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CookieServiceTest {

  private CookiePreferenceRepository repository;
  private CookieService service;

  @BeforeEach
  void setUp() {
    repository = mock(CookiePreferenceRepository.class);
    service = new CookieService(repository);
  }

  @Test
  void setCreatesNewPreferenceWhenMissing() {
    CookiePrefRequest request = new CookiePrefRequest();
    request.setSubjectId("user-1");
    request.setSessionId("session-1");
    request.setAnalytics(Boolean.TRUE);
    request.setMarketing(Boolean.FALSE);
    request.setPersonalization(Boolean.TRUE);

    when(repository.findFirstBySubjectIdOrSessionIdOrderByCreatedAtDesc("user-1", "session-1"))
        .thenReturn(Optional.empty());
    when(repository.save(any(CookiePreference.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CookiePreference saved = service.set(request);

    assertThat(saved.getSubjectId()).isEqualTo("user-1");
    assertThat(saved.getSessionId()).isEqualTo("session-1");
    assertThat(saved.getAnalytics()).isTrue();
    assertThat(saved.getMarketing()).isFalse();
    assertThat(saved.getPersonalization()).isTrue();
    verify(repository).save(saved);
  }

  @Test
  void setUpdatesOnlyProvidedPreferences() {
    CookiePreference existing = new CookiePreference();
    existing.setSubjectId("user-1");
    existing.setSessionId("session-1");
    existing.setAnalytics(false);
    existing.setMarketing(true);
    existing.setPersonalization(true);

    when(repository.findFirstBySubjectIdOrSessionIdOrderByCreatedAtDesc("user-1", "session-1"))
        .thenReturn(Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    CookiePrefRequest request = new CookiePrefRequest();
    request.setSubjectId("user-1");
    request.setSessionId("session-1");
    request.setAnalytics(Boolean.TRUE);
    request.setMarketing(null); // keep existing true
    request.setPersonalization(Boolean.FALSE);

    CookiePreference saved = service.set(request);

    assertThat(saved).isSameAs(existing);
    assertThat(saved.getAnalytics()).isTrue();
    assertThat(saved.getMarketing()).isTrue(); // unchanged
    assertThat(saved.getPersonalization()).isFalse();
    verify(repository).save(existing);
  }
}
