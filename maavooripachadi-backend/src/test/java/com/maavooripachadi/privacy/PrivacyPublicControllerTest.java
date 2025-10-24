package com.maavooripachadi.privacy;

import com.maavooripachadi.privacy.dto.ConsentUpsertRequest;
import com.maavooripachadi.privacy.dto.CookiePrefRequest;
import com.maavooripachadi.privacy.dto.DsrOpenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PrivacyPublicControllerTest {

  private PolicyService policyService;
  private ConsentService consentService;
  private CookieService cookieService;
  private DsrService dsrService;
  private PrivacyExportService exportService;
  private PrivacyPublicController controller;

  @BeforeEach
  void setUp() {
    policyService = mock(PolicyService.class);
    consentService = mock(ConsentService.class);
    cookieService = mock(CookieService.class);
    dsrService = mock(DsrService.class);
    exportService = mock(PrivacyExportService.class);
    controller = new PrivacyPublicController(policyService, consentService, cookieService, dsrService, exportService);
  }

  @Test
  void activePolicyReturnsFromService() {
    PrivacyPolicy policy = new PrivacyPolicy();
    when(policyService.getActive()).thenReturn(policy);

    assertThat(controller.activePolicy()).isSameAs(policy);
  }

  @Test
  void upsertConsentDelegatesToService() {
    ConsentUpsertRequest request = new ConsentUpsertRequest();
    ConsentRecord record = new ConsentRecord();
    when(consentService.upsert(request)).thenReturn(record);

    ConsentRecord response = controller.upsertConsent(request);

    assertThat(response).isSameAs(record);
    verify(consentService).upsert(request);
  }

  @Test
  void setCookiesDelegatesToService() {
    CookiePrefRequest request = new CookiePrefRequest();
    CookiePreference preference = new CookiePreference();
    when(cookieService.set(request)).thenReturn(preference);

    CookiePreference response = controller.setCookies(request);

    assertThat(response).isSameAs(preference);
    verify(cookieService).set(request);
  }

  @Test
  void openDsrDelegatesToService() {
    DsrOpenRequest request = new DsrOpenRequest();
    DsrRequest dsr = new DsrRequest();
    when(dsrService.open(request)).thenReturn(dsr);

    DsrRequest response = controller.openDsr(request);

    assertThat(response).isSameAs(dsr);
    verify(dsrService).open(request);
  }

  @Test
  void exportConsentsReturnsCsvResponse() {
    byte[] csv = "createdAt,...".getBytes();
    when(exportService.exportConsentsCsv("user-1")).thenReturn(csv);

    ResponseEntity<byte[]> response = controller.exportConsents("user-1");

    assertThat(response.getBody()).isEqualTo(csv);
    assertThat(response.getHeaders().getContentType()).isNotNull();
    assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo("consents-user-1.csv");
    verify(exportService).exportConsentsCsv("user-1");
  }
}
