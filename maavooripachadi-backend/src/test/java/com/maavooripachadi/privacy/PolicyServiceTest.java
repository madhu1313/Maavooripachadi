package com.maavooripachadi.privacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PolicyServiceTest {

  private PrivacyPolicyRepository policyRepository;
  private PrivacyEventRepository eventRepository;
  private PolicyService service;

  @BeforeEach
  void setUp() {
    policyRepository = mock(PrivacyPolicyRepository.class);
    eventRepository = mock(PrivacyEventRepository.class);
    service = new PolicyService(policyRepository, eventRepository);
  }

  @Test
  void getActiveReturnsLatestActivePolicy() {
    PrivacyPolicy policy = new PrivacyPolicy();
    when(policyRepository.findFirstByActiveTrueOrderByCreatedAtDesc()).thenReturn(Optional.of(policy));

    assertThat(service.getActive()).isSameAs(policy);
  }

  @Test
  void publishDeactivatesExistingPoliciesWhenActivatingNewDraft() {
    PrivacyPolicy oldActive = new PrivacyPolicy();
    oldActive.setActive(true);
    oldActive.setPolicyVersion("v1");

    PrivacyPolicy draft = new PrivacyPolicy();
    draft.setPolicyVersion("v2");
    draft.setActive(true);

    when(policyRepository.findAll()).thenReturn(List.of(oldActive));
    when(policyRepository.save(oldActive)).thenReturn(oldActive);
    when(policyRepository.save(draft)).thenReturn(draft);
    when(eventRepository.save(any(PrivacyEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

    PrivacyPolicy published = service.publish(draft);

    assertThat(oldActive.getActive()).isFalse();
    verify(policyRepository).save(oldActive);

    assertThat(published).isSameAs(draft);
    verify(policyRepository).save(draft);

    verify(eventRepository).save(argThat(event ->
        "POLICY_PUBLISHED".equals(event.getKind()) &&
            event.getPayloadJson().contains("\"version\":\"v2\"")));
  }
}
