package com.maavooripachadi.content.dto;


import com.maavooripachadi.content.BlogPost;


public record BlogDetail(Long id, String title, String slug, String heroImageUrl,
                         String tags, String bodyHtml, String author){
    public static BlogDetail from(BlogPost b){
        return new BlogDetail(b.getId(), b.getTitle(), b.getSlug(), b.getHeroImageUrl(),
                b.getTags(), b.getBodyHtml(), b.getAuthor());
    }
}