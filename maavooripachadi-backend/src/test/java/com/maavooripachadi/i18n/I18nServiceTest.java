package com.maavooripachadi.i18n;

import com.maavooripachadi.i18n.dto.I18nUpsertRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class I18nServiceTest {

  private I18nStringRepository repository;
  private I18nService service;

  @BeforeEach
  void setUp() {
    repository = mock(I18nStringRepository.class);
    service = new I18nService(repository);
  }

  @Test
  void getBundleUsesFallbackChainAndSkipsUnapprovedStrings() {
    I18nString hero = new I18nString();
    hero.setNamespace("storefront");
    hero.setLocale("en-IN");
    hero.setKey("hero.title");
    hero.setText("Namaste!");
    hero.setApproved(true);

    I18nString fallback = new I18nString();
    fallback.setNamespace("storefront");
    fallback.setLocale("default");
    fallback.setKey("checkout.cta");
    fallback.setText("Checkout");
    fallback.setApproved(true);

    I18nString unapproved = new I18nString();
    unapproved.setNamespace("storefront");
    unapproved.setLocale("en");
    unapproved.setKey("banner.subtitle");
    unapproved.setText("Hidden");
    unapproved.setApproved(false);

    when(repository.findByNamespaceAndLocales(eq("storefront"), anyList()))
        .thenReturn(List.of(hero, fallback, unapproved));

    Map<String, String> bundle = service.getBundle("storefront", "en-IN");

    ArgumentCaptor<List<String>> localesCaptor = ArgumentCaptor.forClass(List.class);
    verify(repository).findByNamespaceAndLocales(eq("storefront"), localesCaptor.capture());
    assertThat(localesCaptor.getValue()).containsExactly("default", "en", "en-IN");

    assertThat(bundle)
        .containsEntry("hero.title", "Namaste!")
        .containsEntry("checkout.cta", "Checkout")
        .doesNotContainKey("banner.subtitle");
  }

  @Test
  void upsertCreatesNewRecordWhenNoExistingEntry() {
    when(repository.findByNamespaceAndKeyAndLocale("storefront", "hero.title", "en"))
        .thenReturn(java.util.Optional.empty());
    when(repository.save(any(I18nString.class))).thenAnswer(invocation -> invocation.getArgument(0));

    I18nUpsertRequest request = new I18nUpsertRequest("storefront", "hero.title", "en", "Hello", "homepage,hero", null);
    I18nString saved = service.upsert(request);

    assertThat(saved.getNamespace()).isEqualTo("storefront");
    assertThat(saved.getKey()).isEqualTo("hero.title");
    assertThat(saved.getLocale()).isEqualTo("en");
    assertThat(saved.getText()).isEqualTo("Hello");
    assertThat(saved.getTags()).isEqualTo("homepage,hero");
    assertThat(saved.getApproved()).isTrue();
    assertThat(saved.getChecksum())
        .isEqualTo(Integer.toHexString(java.util.Objects.hash("Hello", "homepage,hero", "en")));
  }

  @Test
  void upsertUpdatesExistingEntry() {
    I18nString existing = new I18nString();
    existing.setNamespace("storefront");
    existing.setKey("hero.title");
    existing.setLocale("en");
    existing.setText("Old");
    existing.setApproved(false);

    when(repository.findByNamespaceAndKeyAndLocale("storefront", "hero.title", "en"))
        .thenReturn(java.util.Optional.of(existing));
    when(repository.save(existing)).thenReturn(existing);

    I18nUpsertRequest request = new I18nUpsertRequest("storefront", "hero.title", "en", "New Text", null, Boolean.TRUE);
    I18nString saved = service.upsert(request);

    assertThat(saved).isSameAs(existing);
    assertThat(saved.getText()).isEqualTo("New Text");
    assertThat(saved.getApproved()).isTrue();
    assertThat(saved.getChecksum())
        .isEqualTo(Integer.toHexString(java.util.Objects.hash("New Text", null, "en")));
  }

  @Test
  void deleteRemovesExistingEntry() {
    I18nString stored = new I18nString();
    when(repository.findByNamespaceAndKeyAndLocale("storefront", "hero.title", "en"))
        .thenReturn(java.util.Optional.of(stored));

    service.delete("storefront", "hero.title", "en");

    verify(repository).delete(stored);
  }

  @Test
  void exportCsvWithNamespaceAndLocaleEscapesFields() {
    I18nString entry = new I18nString();
    entry.setNamespace("storefront");
    entry.setKey("welcome.message");
    entry.setLocale("en");
    entry.setText("Hello, world");
    entry.setTags("homepage,greeting");
    entry.setApproved(true);

    when(repository.findByNamespaceAndLocale("storefront", "en")).thenReturn(List.of(entry));

    String csv = service.exportCsv("storefront", "en");

    assertThat(csv).isEqualTo("""
        namespace,key,locale,text,tags,approved
        storefront,welcome.message,en,"Hello, world","homepage,greeting",true
        """);
  }

  @Test
  void exportCsvWithNamespaceAndDefaultLocaleUsesFallbackList() {
    when(repository.findByNamespaceAndLocales(eq("storefront"), anyList())).thenReturn(List.of());

    String csv = service.exportCsv("storefront", null);

    verify(repository).findByNamespaceAndLocales("storefront", List.of("default"));
    assertThat(csv).isEqualTo("namespace,key,locale,text,tags,approved\n");
  }
}
