package com.maavooripachadi.cart;

import com.maavooripachadi.catalog.Product;
import com.maavooripachadi.catalog.Variant;
import com.maavooripachadi.catalog.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {
  private final CartRepository carts;
  private final VariantRepository variants;

  @Transactional
  public Cart view(String sessionId){
    var cart = getOrCreate(sessionId);
    return hydrateMetadata(cart);
  }

  @Transactional
  public Cart clear(String sessionId){
    var cart = getOrCreate(sessionId);
    cart.getItems().clear();
    return carts.save(cart);
  }

  @Transactional
  public Cart remove(String sessionId, Long variantId){
    var cart = getOrCreate(sessionId);
    cart.getItems().removeIf(i -> i.getVariantId().equals(variantId));
    return carts.save(cart);
  }

  @Transactional
  public Cart add(String sessionId, Long variantId, int qty, int unitPricePaise){
    if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
    if (unitPricePaise <= 0) throw new IllegalArgumentException("unitPricePaise must be > 0");

    var cart = getOrCreate(sessionId);
    // merge qty when the same variant already exists
    var existing = cart.getItems().stream()
            .filter(i -> i.getVariantId().equals(variantId))
            .findFirst()
            .orElse(null);

    Variant variant = variants.findById(variantId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown variant: " + variantId));
    Product product = variant.getProduct();
    String title = resolveTitle(product, variant);
    String imageUrl = product != null ? product.getHeroImageUrl() : null;

    if (existing != null) {
      existing.setQty(existing.getQty() + qty);
      // keep the latest price if it changed
      existing.setUnitPricePaise(unitPricePaise);
      existing.setTitle(title);
      existing.setImageUrl(imageUrl);
    } else {
      var item = new CartItem();
      item.setCart(cart);
      item.setVariantId(variantId);
      item.setQty(qty);
      item.setUnitPricePaise(unitPricePaise);
      item.setTitle(title);
      item.setImageUrl(imageUrl);
      cart.getItems().add(item);
    }
    return carts.save(cart);
  }

  @Transactional
  protected Cart getOrCreate(String sessionId){
    return carts.findBySessionId(sessionId).orElseGet(() -> {
      var c = new Cart();
      c.setSessionId(sessionId);
      return carts.save(c);
    });
  }

  private Cart hydrateMetadata(Cart cart) {
    boolean updated = false;
    for (CartItem item : cart.getItems()) {
      if (item.getTitle() == null || item.getImageUrl() == null) {
        variants.findById(item.getVariantId()).ifPresent(variant -> {
          Product product = variant.getProduct();
          item.setTitle(resolveTitle(product, variant));
          if (item.getImageUrl() == null && product != null) {
            item.setImageUrl(product.getHeroImageUrl());
          }
        });
        updated = true;
      }
    }
    return updated ? carts.save(cart) : cart;
  }

  private String resolveTitle(Product product, Variant variant) {
    if (product == null) {
      return variant.getLabel() != null ? variant.getLabel() : "Product";
    }
    if (variant.getLabel() != null && !variant.getLabel().isBlank()) {
      return product.getTitle() + " (" + variant.getLabel() + ")";
    }
    return product.getTitle();
  }
}
