package com.maavooripachadi.content.dto;


import com.maavooripachadi.content.BlogPost;


public record BlogCard(Long id, String title, String slug, String heroImageUrl, String excerptHtml, String tags){
    public static BlogCard from(BlogPost b){
        return new BlogCard(b.getId(), b.getTitle(), b.getSlug(), b.getHeroImageUrl(), b.getExcerptHtml(), b.getTags());
    }
}