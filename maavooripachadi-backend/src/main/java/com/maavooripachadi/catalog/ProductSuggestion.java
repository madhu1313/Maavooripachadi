package com.maavooripachadi.catalog;


public record ProductSuggestion(Long id, String title, String slug) {
    static ProductSuggestion from(Product p){
        return new ProductSuggestion(p.getId(), p.getTitle(), p.getSlug()); }
}