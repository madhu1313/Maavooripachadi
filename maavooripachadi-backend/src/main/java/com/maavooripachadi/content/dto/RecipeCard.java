package com.maavooripachadi.content.dto;


import com.maavooripachadi.content.Recipe;


public record RecipeCard(Long id, String title, String slug, String heroImageUrl, String tags){
    public static RecipeCard from(Recipe r){
        return new RecipeCard(r.getId(), r.getTitle(), r.getSlug(), r.getHeroImageUrl(), r.getTags());
    }
}