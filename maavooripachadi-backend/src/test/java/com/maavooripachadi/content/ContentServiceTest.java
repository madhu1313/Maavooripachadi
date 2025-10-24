package com.maavooripachadi.content;

import com.maavooripachadi.content.dto.BlogDetail;
import com.maavooripachadi.content.dto.RecipeCard;
import com.maavooripachadi.content.dto.RecipeDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ContentServiceTest {

  private RecipeRepository recipes;
  private BlogRepository blogs;
  private ContentService service;

  @BeforeEach
  void setUp() {
    recipes = mock(RecipeRepository.class);
    blogs = mock(BlogRepository.class);
    service = new ContentService(recipes, blogs);
  }

  @Test
  void listRecipesUsesDescendingCreatedAtSortAndMapsCards() {
    Recipe recipe = new Recipe();
    recipe.setTitle("Gongura");
    recipe.setSlug("gongura");
    recipe.setHeroImageUrl("hero.jpg");
    recipe.setTags("spicy,vegan");
    ReflectionTestUtils.setField(recipe, "id", 11L);

    when(recipes.search(eq("spicy"), eq("gongura"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(recipe)));

    Page<RecipeCard> cards = service.listRecipes(2, 15, "spicy", "gongura");

    assertThat(cards.getContent()).singleElement()
        .extracting(RecipeCard::id, RecipeCard::slug)
        .containsExactly(11L, "gongura");

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(recipes).search(eq("spicy"), eq("gongura"), pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isEqualTo(2);
    assertThat(pageable.getPageSize()).isEqualTo(15);
    assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
    assertThat(pageable.getSort().getOrderFor("createdAt").isDescending()).isTrue();
  }

  @Test
  void listBlogsUsesDescendingCreatedAtSortAndMapsCards() {
    BlogPost blog = new BlogPost();
    blog.setTitle("Festive Menu");
    blog.setSlug("festive-menu");
    blog.setHeroImageUrl("hero.png");
    blog.setTags("festival");
    blog.setExcerptHtml("<p>Excerpt</p>");
    ReflectionTestUtils.setField(blog, "id", 25L);

    when(blogs.search(eq("festival"), eq("menu"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(blog)));

    Page<com.maavooripachadi.content.dto.BlogCard> cards = service.listBlogs(0, 5, "festival", "menu");

    assertThat(cards.getContent()).singleElement()
        .extracting(com.maavooripachadi.content.dto.BlogCard::id, com.maavooripachadi.content.dto.BlogCard::slug)
        .containsExactly(25L, "festive-menu");

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(blogs).search(eq("festival"), eq("menu"), pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getSort().getOrderFor("createdAt")).isNotNull();
    assertThat(pageable.getSort().getOrderFor("createdAt").isDescending()).isTrue();
  }

  @Test
  void recipeBySlugReturnsDetailedDto() {
    Recipe recipe = new Recipe();
    recipe.setTitle("Natu Kodi");
    recipe.setSlug("natu-kodi");
    recipe.setHeroImageUrl("hero");
    recipe.setTags("nonveg");
    recipe.setIntroHtml("intro");
    recipe.setIngredientsHtml("ingredients");
    recipe.setStepsHtml("steps");
    recipe.setAuthor("Chef");
    ReflectionTestUtils.setField(recipe, "id", 7L);

    when(recipes.findBySlugAndPublishedTrue("natu-kodi")).thenReturn(Optional.of(recipe));

    RecipeDetail detail = service.recipeBySlug("natu-kodi");

    assertThat(detail.id()).isEqualTo(7L);
    assertThat(detail.slug()).isEqualTo("natu-kodi");
    assertThat(detail.author()).isEqualTo("Chef");
  }

  @Test
  void recipeBySlugThrowsWhenMissing() {
    when(recipes.findBySlugAndPublishedTrue("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.recipeBySlug("missing"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("missing");
  }

  @Test
  void blogBySlugReturnsDetailedDto() {
    BlogPost blog = new BlogPost();
    blog.setTitle("Summer Specials");
    blog.setSlug("summer-specials");
    blog.setHeroImageUrl("hero");
    blog.setTags("summer");
    blog.setBodyHtml("<p>body</p>");
    blog.setAuthor("Team");
    ReflectionTestUtils.setField(blog, "id", 33L);

    when(blogs.findBySlugAndPublishedTrue("summer-specials")).thenReturn(Optional.of(blog));

    BlogDetail detail = service.blogBySlug("summer-specials");

    assertThat(detail.id()).isEqualTo(33L);
    assertThat(detail.slug()).isEqualTo("summer-specials");
    assertThat(detail.author()).isEqualTo("Team");
  }

  @Test
  void blogBySlugThrowsWhenMissing() {
    when(blogs.findBySlugAndPublishedTrue("gone")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.blogBySlug("gone"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("gone");
  }

  @Test
  void publishRecipeMarksEntityPublished() {
    Recipe recipe = new Recipe();
    recipe.setPublished(false);
    ReflectionTestUtils.setField(recipe, "id", 44L);

    when(recipes.findById(44L)).thenReturn(Optional.of(recipe));

    service.publish("recipe", 44L);

    assertThat(recipe.getPublished()).isTrue();
    verify(recipes).save(recipe);
    verifyNoInteractions(blogs);
  }

  @Test
  void publishBlogMarksEntityPublished() {
    BlogPost blog = new BlogPost();
    blog.setPublished(false);
    ReflectionTestUtils.setField(blog, "id", 91L);

    when(blogs.findById(91L)).thenReturn(Optional.of(blog));

    service.publish("blog", 91L);

    assertThat(blog.getPublished()).isTrue();
    verify(blogs).save(blog);
    verifyNoMoreInteractions(recipes);
  }

  @Test
  void publishRejectsUnknownType() {
    assertThatThrownBy(() -> service.publish("video", 1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unknown type");
  }

  @Test
  void unpublishRecipeMarksEntityUnpublished() {
    Recipe recipe = new Recipe();
    recipe.setPublished(true);
    ReflectionTestUtils.setField(recipe, "id", 55L);

    when(recipes.findById(55L)).thenReturn(Optional.of(recipe));

    service.unpublish("recipe", 55L);

    assertThat(recipe.getPublished()).isFalse();
    verify(recipes).save(recipe);
  }

  @Test
  void unpublishBlogMarksEntityUnpublished() {
    BlogPost blog = new BlogPost();
    blog.setPublished(true);
    ReflectionTestUtils.setField(blog, "id", 78L);

    when(blogs.findById(78L)).thenReturn(Optional.of(blog));

    service.unpublish("blog", 78L);

    assertThat(blog.getPublished()).isFalse();
    verify(blogs).save(blog);
  }
}
