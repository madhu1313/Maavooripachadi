package com.maavooripachadi.content;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "recipe")
public class Recipe extends BaseEntity {
  @Column(unique = true, nullable = false)
  private String slug;

  @Column(nullable = false)
  private String title;

  private String heroImageUrl;
  private String tags; // comma separated

  @Lob
  private String introHtml;

  @Lob
  private String ingredientsHtml;

  @Lob
  private String stepsHtml;

  private Boolean published = false;
  private String author;

  public Recipe() {
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

  public String getIntroHtml() {
    return introHtml;
  }

  public void setIntroHtml(String introHtml) {
    this.introHtml = introHtml;
  }

  public String getIngredientsHtml() {
    return ingredientsHtml;
  }

  public void setIngredientsHtml(String ingredientsHtml) {
    this.ingredientsHtml = ingredientsHtml;
  }

  public String getStepsHtml() {
    return stepsHtml;
  }

  public void setStepsHtml(String stepsHtml) {
    this.stepsHtml = stepsHtml;
  }

  public Boolean getPublished() {
    return published;
  }

  public void setPublished(Boolean published) {
    this.published = published;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }
}
