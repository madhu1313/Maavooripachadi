package com.maavooripachadi.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CatalogServiceTest {

  private ProductRepository productRepository;
  private VariantRepository variantRepository;
  private CatalogService service;

  @BeforeEach
  void setUp() {
    productRepository = mock(ProductRepository.class);
    variantRepository = mock(VariantRepository.class);
    service = new CatalogService(productRepository, variantRepository);
  }

  @Test
  void listDelegatesToRepositoryWithSortAndMapsProductCards() {
    Product product = new Product();
    ReflectionTestUtils.setField(product, "id", 1L);
    product.setSlug("ginger-pickle");
    product.setTitle("Ginger Pickle");
    product.setHeroImageUrl("/img.png");
    product.setPricePaise(4999);
    product.setMrpPaise(5999);
    product.setInStock(true);
    product.setBadge("Bestseller");

    Page<Product> page = new PageImpl<>(List.of(product));
    when(productRepository.search(eq("pickles"), eq("spicy"), eq("ginger"), eq(1000), eq(5000), any(Pageable.class)))
        .thenReturn(page);

    Page<ProductCard> result = service.list(1, 20, "price,asc", "pickles", "spicy", "ginger", 1000, 5000);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(productRepository).search(eq("pickles"), eq("spicy"), eq("ginger"), eq(1000), eq(5000), pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isEqualTo(1);
    assertThat(pageable.getPageSize()).isEqualTo(20);
    assertThat(pageable.getSort()).extracting("property").containsExactly("pricePaise");
    assertThat(pageable.getSort().getOrderFor("pricePaise").getDirection()).isEqualTo(org.springframework.data.domain.Sort.Direction.ASC);

    assertThat(result.getContent())
        .singleElement()
        .extracting(ProductCard::slug, ProductCard::pricePaise, ProductCard::badge)
        .containsExactly("ginger-pickle", 4999, "Bestseller");
  }

  @Test
  void detailReturnsProductWithVariants() {
    Product product = new Product();
    ReflectionTestUtils.setField(product, "id", 10L);
    product.setSlug("ginger");
    product.setTitle("Ginger Pickle");
    product.setDescriptionHtml("<p>Tasty</p>");
    product.setHeroImageUrl("/ginger.jpg");
    product.setPricePaise(3999);
    product.setMrpPaise(4999);
    product.setBadge("Hot");

    Variant variant = new Variant();
    ReflectionTestUtils.setField(variant, "id", 100L);
    variant.setSku("GINGER-250");
    variant.setLabel("250g");
    variant.setPricePaise(3999);
    variant.setInStock(true);

    when(productRepository.findBySlug("ginger")).thenReturn(java.util.Optional.of(product));
    when(variantRepository.findByProductId(10L)).thenReturn(List.of(variant));

    ProductDetail detail = service.detail("ginger");

    assertThat(detail.slug()).isEqualTo("ginger");
    assertThat(detail.variants()).hasSize(1);
    assertThat(detail.variants().getFirst().sku()).isEqualTo("GINGER-250");
  }

  @Test
  void detailThrowsNotFoundWhenSlugMissing() {
    when(productRepository.findBySlug("missing")).thenReturn(java.util.Optional.empty());

    assertThatThrownBy(() -> service.detail("missing"))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("Product not found");
  }

  @Test
  void suggestReturnsMappedSuggestions() {
    Product product = new Product();
    ReflectionTestUtils.setField(product, "id", 25L);
    product.setSlug("garlic");
    product.setTitle("Garlic Pickle");
    product.setHeroImageUrl("/garlic.jpg");

    when(productRepository.findTopByTitleContainingIgnoreCaseOrTagsContainingIgnoreCase(eq("gar"), eq("gar"), any(Pageable.class)))
        .thenReturn(List.of(product));

    List<ProductSuggestion> suggestions = service.suggest("gar", 5);

    assertThat(suggestions)
        .singleElement()
        .extracting(ProductSuggestion::slug, ProductSuggestion::title)
        .containsExactly("garlic", "Garlic Pickle");
  }
}
