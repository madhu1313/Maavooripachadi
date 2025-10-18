package com.maavooripachadi.catalog;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@Validated
public class CatalogController {
  private final CatalogService catalog;


  /**
   * List products with optional filters.
   */
  @GetMapping("/products")
  public Page<ProductCard> list(
          @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
          @RequestParam(value = "size", defaultValue = "12") @Min(1) @Max(60) int size,
          @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort,
          @RequestParam(value = "category", required = false) @Size(min = 1, max = 64) String category,
          @RequestParam(value = "tag", required = false) @Size(min = 1, max = 64) String tag,
          @RequestParam(value = "q", required = false) @Size(min = 1, max = 64) String q,
          @RequestParam(value = "minPricePaise", required = false) Integer minPricePaise,
          @RequestParam(value = "maxPricePaise", required = false) Integer maxPricePaise
  ){
    return catalog.list(page, size, sort, category, tag, q, minPricePaise, maxPricePaise);
  }


  /**
   * Product detail (by slug) including variants.
   */
  @GetMapping("/products/{slug}")
  public ProductDetail detail(@PathVariable String slug){
    return catalog.detail(slug);
  }


  /** Simple typeahead search for header search box */
  @GetMapping("/search")
  public java.util.List<ProductSuggestion> search(@RequestParam("q") @Size(min=2, max=64) String q){
    return catalog.suggest(q, 8);
  }
}
