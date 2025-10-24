package com.maavooripachadi.privacy;

import com.maavooripachadi.privacy.dto.DsrDecisionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PrivacyAdminControllerTest {

  private PolicyService policyService;
  private DsrService dsrService;
  private PrivacyAdminController controller;

  @BeforeEach
  void setUp() {
    policyService = mock(PolicyService.class);
    dsrService = mock(DsrService.class);
    controller = new PrivacyAdminController(policyService, dsrService);
  }

  @Test
  void publishDelegatesToPolicyService() {
    PrivacyPolicy policy = new PrivacyPolicy();
    when(policyService.publish(policy)).thenReturn(policy);

    PrivacyPolicy response = controller.publish(policy);

    assertThat(response).isSameAs(policy);
    verify(policyService).publish(policy);
  }

  @Test
  void decideDelegatesToDsrService() {
    DsrDecisionRequest request = new DsrDecisionRequest();
    request.setStatus(DsrStatus.COMPLETED);
    DsrRequest dsr = new DsrRequest();
    when(dsrService.decide(42L, request)).thenReturn(dsr);

    DsrRequest response = controller.decide(42L, request);

    assertThat(response).isSameAs(dsr);
    verify(dsrService).decide(42L, request);
  }
}
