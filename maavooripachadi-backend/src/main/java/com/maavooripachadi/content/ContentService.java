package com.maavooripachadi.content;


import com.maavooripachadi.content.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ContentService {
    private final RecipeRepository recipes;
    private final BlogRepository blogs;


    public Page<RecipeCard> listRecipes(int page, int size, String tag, String q){
        return recipes.search(tag, q, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(RecipeCard::from);
    }


    public RecipeDetail recipeBySlug(String slug){
        return recipes.findBySlugAndPublishedTrue(slug).map(RecipeDetail::from)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found: "+slug));
    }


    public Page<BlogCard> listBlogs(int page, int size, String tag, String q){
        return blogs.search(tag, q, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(BlogCard::from);
    }


    public BlogDetail blogBySlug(String slug){
        return blogs.findBySlugAndPublishedTrue(slug).map(BlogDetail::from)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found: "+slug));
    }


    @Transactional
    public void publish(String type, long id){
        switch (type.toUpperCase()){
            case "RECIPE" -> recipes.findById(id).ifPresent(r -> { r.setPublished(true); recipes.save(r); });
            case "BLOG" -> blogs.findById(id).ifPresent(b -> { b.setPublished(true); blogs.save(b); });
            default -> throw new IllegalArgumentException("Unknown type: "+type);
        }
    }


    @Transactional
    public void unpublish(String type, long id){
        switch (type.toUpperCase()){
            case "RECIPE" -> recipes.findById(id).ifPresent(r -> { r.setPublished(false); recipes.save(r); });
            case "BLOG" -> blogs.findById(id).ifPresent(b -> { b.setPublished(false); blogs.save(b); });
            default -> throw new IllegalArgumentException("Unknown type: "+type);
        }
    }
}