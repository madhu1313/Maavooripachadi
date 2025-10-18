package com.maavooripachadi.catalog;


public record ProductCard(
        Long id,
        String title,
        String slug,
        String imageUrl,
        int pricePaise,
        Integer mrpPaise,
        boolean inStock,
        String badge
) {
    static ProductCard from(Product p){
        return new ProductCard(
                p.getId(), p.getTitle(), p.getSlug(), p.getHeroImageUrl(), p.getPricePaise(), p.getMrpPaise(), p.isInStock(), p.getBadge()
        );
    }
}