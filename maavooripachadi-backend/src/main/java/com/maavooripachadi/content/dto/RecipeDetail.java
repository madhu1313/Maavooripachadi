package com.maavooripachadi.content.dto;


import com.maavooripachadi.content.Recipe;


public record RecipeDetail(Long id, String title, String slug, String heroImageUrl,
                           String tags, String introHtml, String ingredientsHtml, String stepsHtml,
                           String author){
    public static RecipeDetail from(Recipe r){
        return new RecipeDetail(r.getId(), r.getTitle(), r.getSlug(), r.getHeroImageUrl(), r.getTags(),
                r.getIntroHtml(), r.getIngredientsHtml(), r.getStepsHtml(), r.getAuthor());
    }
}