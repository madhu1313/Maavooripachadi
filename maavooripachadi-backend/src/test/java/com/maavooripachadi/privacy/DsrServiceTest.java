package com.maavooripachadi.privacy;

import com.maavooripachadi.privacy.dto.DsrDecisionRequest;
import com.maavooripachadi.privacy.dto.DsrOpenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DsrServiceTest {

  private DsrRequestRepository repository;
  private PrivacyEventRepository eventRepository;
  private DsrService service;

  @BeforeEach
  void setUp() {
    repository = mock(DsrRequestRepository.class);
    eventRepository = mock(PrivacyEventRepository.class);
    service = new DsrService(repository, eventRepository);
  }

  @Test
  void openCreatesRequestAndEmitsEvent() {
    DsrOpenRequest request = new DsrOpenRequest();
    request.setType(DsrType.ACCESS);
    request.setSubjectId("user-1");
    request.setDetails("Give me my data");

    when(repository.save(any(DsrRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(eventRepository.save(any(PrivacyEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

    DsrRequest saved = service.open(request);

    ArgumentCaptor<DsrRequest> requestCaptor = ArgumentCaptor.forClass(DsrRequest.class);
    verify(repository).save(requestCaptor.capture());
    assertThat(requestCaptor.getValue().getType()).isEqualTo(DsrType.ACCESS);
    assertThat(requestCaptor.getValue().getSubjectId()).isEqualTo("user-1");
    assertThat(requestCaptor.getValue().getDetails()).isEqualTo("Give me my data");

    ArgumentCaptor<PrivacyEvent> eventCaptor = ArgumentCaptor.forClass(PrivacyEvent.class);
    verify(eventRepository).save(eventCaptor.capture());
    PrivacyEvent event = eventCaptor.getValue();
    assertThat(event.getKind()).isEqualTo("DSR_OPENED");
    assertThat(event.getPayloadJson()).contains("\"type\":\"ACCESS\"");

    assertThat(saved).isSameAs(requestCaptor.getValue());
  }

  @Test
  void decideUpdatesRequestAndRecordsEvent() {
    DsrRequest existing = new DsrRequest();
    existing.setSubjectId("user-1");
    existing.setDetails("Initial");

    when(repository.findById(50L)).thenReturn(java.util.Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    DsrDecisionRequest decision = new DsrDecisionRequest();
    decision.setStatus(DsrStatus.COMPLETED);
    decision.setDetails("Approved on call");

    service.decide(50L, decision);

    assertThat(existing.getStatus()).isEqualTo(DsrStatus.COMPLETED);
    assertThat(existing.getDetails()).isEqualTo("Approved on call");

    ArgumentCaptor<PrivacyEvent> eventCaptor = ArgumentCaptor.forClass(PrivacyEvent.class);
    verify(eventRepository).save(eventCaptor.capture());
    PrivacyEvent event = eventCaptor.getValue();
    assertThat(event.getKind()).isEqualTo("DSR_DECIDED");
    assertThat(event.getPayloadJson()).contains("\"status\":\"" + decision.getStatus().name() + "\"");
  }
}
