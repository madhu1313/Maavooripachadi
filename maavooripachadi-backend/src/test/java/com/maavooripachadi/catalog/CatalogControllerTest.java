package com.maavooripachadi.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CatalogControllerTest {

  private CatalogService service;
  private CatalogController controller;

  @BeforeEach
  void setUp() {
    service = mock(CatalogService.class);
    controller = new CatalogController(service);
  }

  @Test
  void listEndpointDelegatesToServiceWithQueryParams() {
    Page<ProductCard> page = new PageImpl<>(List.of(new ProductCard(1L, "Pickle", "pickle", "/img", 1000, null, true, null)));
    when(service.list(0, 12, "createdAt,desc", null, "spicy", null, null, null)).thenReturn(page);

    Page<ProductCard> response = controller.list(0, 12, "createdAt,desc", null, "spicy", null, null, null);

    assertThat(response).isSameAs(page);
    verify(service).list(0, 12, "createdAt,desc", null, "spicy", null, null, null);
  }

  @Test
  void detailEndpointDelegatesToService() {
    ProductDetail detail = new ProductDetail(1L, "Pickle", "pickle", "<p>desc</p>", "/img", 1000, null, true, null, List.of());
    when(service.detail("pickle")).thenReturn(detail);

    ProductDetail response = controller.detail("pickle");

    assertThat(response).isSameAs(detail);
    verify(service).detail("pickle");
  }

  @Test
  void searchEndpointCallsSuggestWithDefaultLimit() {
    List<ProductSuggestion> suggestions = List.of(new ProductSuggestion(1L, "Pickle", "pickle"));
    when(service.suggest("pi", 8)).thenReturn(suggestions);

    List<ProductSuggestion> response = controller.search("pi");

    assertThat(response).isEqualTo(suggestions);
    verify(service).suggest("pi", 8);
  }
}
