package com.maavooripachadi.catalog;

import java.util.List;


public record ProductDetail(
        Long id,
        String title,
        String slug,
        String descriptionHtml,
        String heroImageUrl,
        int pricePaise,
        Integer mrpPaise,
        boolean inStock,
        String badge,
        List<VariantDto> variants
) {
    static ProductDetail from(Product p, java.util.List<Variant> vs){
        return new ProductDetail(
                p.getId(), p.getTitle(), p.getSlug(), p.getDescriptionHtml(), p.getHeroImageUrl(), p.getPricePaise(), p.getMrpPaise(), p.isInStock(), p.getBadge(),
                vs.stream().map(VariantDto::from).toList()
        );
    }
    public record VariantDto(Long id, String sku, String label, int pricePaise, boolean inStock){
        static VariantDto from(Variant v){ return new VariantDto(v.getId(), v.getSku(), v.getLabel(), v.getPricePaise(), v.isInStock()); }
    }
}
