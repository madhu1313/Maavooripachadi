package com.maavooripachadi.cart;

import com.maavooripachadi.catalog.Product;
import com.maavooripachadi.catalog.Variant;
import com.maavooripachadi.catalog.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartRepository cartRepository;
    private VariantRepository variantRepository;
    private CartService service;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        variantRepository = mock(VariantRepository.class);
        service = new CartService(cartRepository, variantRepository);

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void addCreatesNewCartItemWhenVariantNotPresent() {
        Cart newCart = new Cart();
        newCart.setSessionId("session-1");

        when(cartRepository.findBySessionId("session-1")).thenReturn(Optional.of(newCart));
        when(variantRepository.findById(42L)).thenReturn(Optional.of(buildVariant("Natu Kodi Pickle", "500 g jar", "image.jpg")));

        Cart result = service.add("session-1", 42L, 2, 25900);

        assertThat(result.getItems()).hasSize(1);
        CartItem item = result.getItems().getFirst();
        assertThat(item.getVariantId()).isEqualTo(42L);
        assertThat(item.getQty()).isEqualTo(2);
        assertThat(item.getUnitPricePaise()).isEqualTo(25900);
        assertThat(item.getTitle()).isEqualTo("Natu Kodi Pickle (500 g jar)");
        assertThat(item.getImageUrl()).isEqualTo("image.jpg");

        verify(cartRepository, times(1)).findBySessionId("session-1");
        verify(cartRepository, times(1)).save(result);
    }

    @Test
    void addMergesExistingItemAndUpdatesDetails() {
        Cart cart = new Cart();
        cart.setSessionId("session-2");
        CartItem existing = new CartItem();
        existing.setCart(cart);
        existing.setVariantId(7L);
        existing.setQty(1);
        existing.setUnitPricePaise(100);
        cart.getItems().add(existing);

        when(cartRepository.findBySessionId("session-2")).thenReturn(Optional.of(cart));
        when(variantRepository.findById(7L)).thenReturn(Optional.of(buildVariant("Prawns Pickle", "250 g jar", "prawns.jpg")));

        Cart updated = service.add("session-2", 7L, 3, 29900);

        assertThat(updated.getItems()).hasSize(1);
        CartItem merged = updated.getItems().getFirst();
        assertThat(merged.getQty()).isEqualTo(4);
        assertThat(merged.getUnitPricePaise()).isEqualTo(29900);
        assertThat(merged.getTitle()).isEqualTo("Prawns Pickle (250 g jar)");
        assertThat(merged.getImageUrl()).isEqualTo("prawns.jpg");
    }

    @Test
    void addRejectsInvalidQuantityOrPrice() {
        when(cartRepository.findBySessionId("s")).thenReturn(Optional.of(new Cart()));
        assertThatThrownBy(() -> service.add("s", 1L, 0, 100)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.add("s", 1L, 1, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addFailsWhenVariantUnknown() {
        when(cartRepository.findBySessionId("session")).thenReturn(Optional.of(new Cart()));
        when(variantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.add("session", 99L, 1, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown variant: 99");
    }

    @Test
    void removeDeletesMatchingItem() {
        Cart cart = new Cart();
        cart.setSessionId("session-remove");
        CartItem keep = new CartItem();
        keep.setCart(cart);
        keep.setVariantId(1L);
        CartItem remove = new CartItem();
        remove.setCart(cart);
        remove.setVariantId(2L);
        cart.getItems().addAll(List.of(keep, remove));

        when(cartRepository.findBySessionId("session-remove")).thenReturn(Optional.of(cart));

        Cart updated = service.remove("session-remove", 2L);

        assertThat(updated.getItems()).extracting(CartItem::getVariantId).containsExactly(1L);
        verify(cartRepository).save(cart);
    }

    @Test
    void clearRemovesAllItems() {
        Cart cart = new Cart();
        cart.setSessionId("session-clear");
        cart.getItems().add(new CartItem());
        cart.getItems().add(new CartItem());

        when(cartRepository.findBySessionId("session-clear")).thenReturn(Optional.of(cart));

        Cart cleared = service.clear("session-clear");

        assertThat(cleared.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }

    @Test
    void viewHydratesMissingMetadataFromVariant() {
        Cart cart = new Cart();
        cart.setSessionId("session-view");
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setVariantId(5L);
        item.setQty(1);
        item.setUnitPricePaise(999);
        cart.getItems().add(item);

        when(cartRepository.findBySessionId("session-view")).thenReturn(Optional.of(cart));
        when(variantRepository.findById(5L)).thenReturn(Optional.of(buildVariant("Gongura Pickle", "", "gongura.jpg")));

        Cart hydrated = service.view("session-view");

        assertThat(hydrated.getItems())
            .singleElement()
            .satisfies(i -> {
                assertThat(i.getTitle()).isEqualTo("Gongura Pickle");
                assertThat(i.getImageUrl()).isEqualTo("gongura.jpg");
            });
        verify(cartRepository).save(cart);
    }

    @Test
    void viewDoesNotSaveCartWhenMetadataAlreadyPresent() {
        Cart cart = new Cart();
        cart.setSessionId("session-view-noop");
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setVariantId(3L);
        item.setTitle("Ready title");
        item.setImageUrl("img");
        cart.getItems().add(item);

        when(cartRepository.findBySessionId("session-view-noop")).thenReturn(Optional.of(cart));

        Cart viewed = service.view("session-view-noop");

        assertThat(viewed.getItems()).hasSize(1);
        verify(variantRepository, never()).findById(any());
        verify(cartRepository, never()).save(cart);
    }

    @Test
    void getOrCreatePersistsNewCartWhenAbsent() {
        when(cartRepository.findBySessionId("new-session")).thenReturn(Optional.empty());

        service.getOrCreate("new-session");

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        assertThat(captor.getValue().getSessionId()).isEqualTo("new-session");
    }

    private Variant buildVariant(String productTitle, String label, String heroImage) {
        Product product = new Product();
        product.setTitle(productTitle);
        product.setHeroImageUrl(heroImage);

        Variant variant = new Variant();
        variant.setProduct(product);
        variant.setLabel(label);
        return variant;
    }
}
