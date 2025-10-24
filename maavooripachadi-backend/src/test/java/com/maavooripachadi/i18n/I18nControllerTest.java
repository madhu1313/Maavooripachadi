package com.maavooripachadi.i18n;

import com.maavooripachadi.i18n.dto.I18nBundleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class I18nControllerTest {

  private I18nService service;
  private I18nController controller;

  @BeforeEach
  void setUp() {
    service = mock(I18nService.class);
    controller = new I18nController(service);
  }

  @Test
  void bundleReturnsServiceBundleAsResponseRecord() {
    Map<String, String> entries = Map.of("welcome.message", "Hello");
    when(service.getBundle("storefront", "en")).thenReturn(entries);

    I18nBundleResponse response = controller.bundle("storefront", "en");

    assertThat(response.namespace()).isEqualTo("storefront");
    assertThat(response.locale()).isEqualTo("en");
    assertThat(response.entries()).isEqualTo(entries);
    verify(service).getBundle("storefront", "en");
  }
}
