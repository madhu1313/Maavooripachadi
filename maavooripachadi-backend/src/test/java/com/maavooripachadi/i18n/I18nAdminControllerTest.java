package com.maavooripachadi.i18n;

import com.maavooripachadi.i18n.dto.I18nDeleteRequest;
import com.maavooripachadi.i18n.dto.I18nImportCsvRequest;
import com.maavooripachadi.i18n.dto.I18nUpsertRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class I18nAdminControllerTest {

  private I18nService service;
  private I18nStringRepository repository;
  private I18nAdminController controller;

  @BeforeEach
  void setUp() {
    service = mock(I18nService.class);
    repository = mock(I18nStringRepository.class);
    controller = new I18nAdminController(service, repository);
  }

  @Test
  void upsertDelegatesToService() {
    I18nUpsertRequest request = new I18nUpsertRequest("storefront", "hero.title", "en", "Hello", null, true);
    I18nString stored = new I18nString();
    when(service.upsert(request)).thenReturn(stored);

    I18nString response = controller.upsert(request);

    assertThat(response).isSameAs(stored);
    verify(service).upsert(request);
  }

  @Test
  void deleteInvokesServiceAndReturnsOkMap() {
    I18nDeleteRequest request = new I18nDeleteRequest("storefront", "hero.title", "en");

    Map<String, Object> response = controller.delete(request);

    assertThat(response).containsEntry("ok", true);
    verify(service).delete("storefront", "hero.title", "en");
  }

  @Test
  void importCsvReturnsCountFromService() throws IOException {
    I18nImportCsvRequest request = new I18nImportCsvRequest("namespace,key,locale,text\n");
    when(service.importCsv(request.csv())).thenReturn(3);

    Map<String, Object> response = controller.importCsv(request);

    assertThat(response).containsEntry("imported", 3);
    verify(service).importCsv(request.csv());
  }

  @Test
  void exportReturnsCsvFromService() {
    when(service.exportCsv("storefront", "en")).thenReturn("csv-data");

    String csv = controller.export("storefront", "en");

    assertThat(csv).isEqualTo("csv-data");
    verify(service).exportCsv("storefront", "en");
  }
}
