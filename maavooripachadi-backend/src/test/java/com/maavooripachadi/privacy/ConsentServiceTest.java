package com.maavooripachadi.privacy;

import com.maavooripachadi.privacy.dto.ConsentUpsertRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConsentServiceTest {

  private ConsentRecordRepository consentRepository;
  private PrivacyEventRepository eventRepository;
  private ConsentService service;

  @BeforeEach
  void setUp() {
    consentRepository = mock(ConsentRecordRepository.class);
    eventRepository = mock(PrivacyEventRepository.class);
    service = new ConsentService(consentRepository, eventRepository);
  }

  @Test
  void upsertPersistsConsentRecordAndEvent() {
    ConsentUpsertRequest request = new ConsentUpsertRequest();
    request.setSubjectId("user-1");
    request.setSessionId("session-1");
    request.setCategory(ConsentCategory.MARKETING);
    request.setStatus(ConsentStatus.GRANTED);
    request.setPolicyVersion("v2");
    request.setSource("SETTINGS");

    when(consentRepository.save(any(ConsentRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(eventRepository.save(any(PrivacyEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ConsentRecord saved = service.upsert(request);

    ArgumentCaptor<ConsentRecord> consentCaptor = ArgumentCaptor.forClass(ConsentRecord.class);
    verify(consentRepository).save(consentCaptor.capture());
    ConsentRecord persisted = consentCaptor.getValue();
    assertThat(persisted.getSubjectId()).isEqualTo("user-1");
    assertThat(persisted.getSessionId()).isEqualTo("session-1");
    assertThat(persisted.getCategory()).isEqualTo(ConsentCategory.MARKETING);
    assertThat(persisted.getStatus()).isEqualTo(ConsentStatus.GRANTED);
    assertThat(persisted.getPolicyVersion()).isEqualTo("v2");
    assertThat(persisted.getSource()).isEqualTo("SETTINGS");

    ArgumentCaptor<PrivacyEvent> eventCaptor = ArgumentCaptor.forClass(PrivacyEvent.class);
    verify(eventRepository).save(eventCaptor.capture());
    PrivacyEvent event = eventCaptor.getValue();
    assertThat(event.getSubjectId()).isEqualTo("user-1");
    assertThat(event.getKind()).isEqualTo("CONSENT_CHANGED");
    assertThat(event.getPayloadJson())
        .contains("\"category\":\"" + request.getCategory().name() + "\"")
        .contains("\"status\":\"" + request.getStatus().name() + "\"");

    assertThat(saved).isSameAs(persisted);
  }
}
