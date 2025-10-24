package com.maavooripachadi.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CartControllerTest {

    private CartService cartService;
    private CartController controller;

    @BeforeEach
    void setUp() {
        cartService = mock(CartService.class);
        controller = new CartController(cartService);
    }

    @Test
    void addEndpointDelegatesAndReturnsResponse() {
        Cart cart = sampleCart();
        when(cartService.add("sess-1", 10L, 2, 150)).thenReturn(cart);

        CartResponse response = controller.add(new AddToCartRequest("sess-1", 10L, 2, 150));

        assertThat(response.sessionId()).isEqualTo("sess-1");
        assertThat(response.itemsCount()).isEqualTo(3);
        assertThat(response.subtotalPaise()).isEqualTo(450);
        verify(cartService).add("sess-1", 10L, 2, 150);
    }

    @Test
    void removeEndpointReturnsMappedResponse() {
        Cart cart = sampleCart();
        when(cartService.remove("sess-2", 11L)).thenReturn(cart);

        CartResponse response = controller.remove(new RemoveFromCartRequest("sess-2", 11L));

        assertThat(response.sessionId()).isEqualTo("sess-1");
        verify(cartService).remove("sess-2", 11L);
    }

    @Test
    void clearEndpointClearsCart() {
        Cart cart = sampleCart();
        when(cartService.clear("sess-3")).thenReturn(cart);

        CartResponse response = controller.clear("sess-3");

        assertThat(response.items()).hasSize(2);
        verify(cartService).clear("sess-3");
    }

    @Test
    void viewEndpointReturnsCurrentCart() {
        Cart cart = sampleCart();
        when(cartService.view("sess-4")).thenReturn(cart);

        CartResponse response = controller.view("sess-4");

        assertThat(response.items()).extracting(CartResponse.Item::variantId).containsExactly(1L, 2L);
        verify(cartService).view("sess-4");
    }

    private Cart sampleCart() {
        Cart cart = new Cart();
        cart.setSessionId("sess-1");
        ReflectionTestUtils.setField(cart, "id", 99L);

        CartItem first = new CartItem(cart, 1L, 1, 150, "Item A", "a.jpg");
        CartItem second = new CartItem(cart, 2L, 2, 150, "Item B", "b.jpg");
        cart.getItems().addAll(List.of(first, second));
        return cart;
    }
}
