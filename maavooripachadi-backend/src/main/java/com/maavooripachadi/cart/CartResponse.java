package com.maavooripachadi.cart;

import java.util.List;

public record CartResponse(
        Long id,
        String sessionId,
        int itemsCount,
        int subtotalPaise,
        List<Item> items
) {
    public static CartResponse from(Cart c){
        int subtotal = c.getItems().stream().mapToInt(i -> i.getQty() * i.getUnitPricePaise()).sum();
        int itemsCount = c.getItems().stream().mapToInt(CartItem::getQty).sum();
        return new CartResponse(
                c.getId(),
                c.getSessionId(),
                itemsCount,
                subtotal,
                c.getItems().stream().map(Item::from).toList()
        );
    }
    public record Item(Long variantId, String title, String imageUrl, int qty, int unitPricePaise){
        static Item from(CartItem i){
            return new Item(i.getVariantId(), i.getTitle(), i.getImageUrl(), i.getQty(), i.getUnitPricePaise());
        }
    }
}
