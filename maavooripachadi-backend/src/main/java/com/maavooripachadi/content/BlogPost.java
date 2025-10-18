package com.maavooripachadi.content;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "blog_post")
public class BlogPost extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String slug;

  @Column(nullable = false)
  private String title;

  private String heroImageUrl;
  private String tags; // comma separated
  private String author;

  @Lob
  private String excerptHtml;

  @Lob
  private String bodyHtml;

  private Boolean published = false;

  public BlogPost() {
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getHeroImageUrl() {
    return heroImageUrl;
  }

  public void setHeroImageUrl(String heroImageUrl) {
    this.heroImageUrl = heroImageUrl;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getExcerptHtml() {
    return excerptHtml;
  }

  public void setExcerptHtml(String excerptHtml) {
    this.excerptHtml = excerptHtml;
  }

  public String getBodyHtml() {
    return bodyHtml;
  }

  public void setBodyHtml(String bodyHtml) {
    this.bodyHtml = bodyHtml;
  }

  public Boolean getPublished() {
    return published;
  }

  public void setPublished(Boolean published) {
    this.published = published;
  }
}
