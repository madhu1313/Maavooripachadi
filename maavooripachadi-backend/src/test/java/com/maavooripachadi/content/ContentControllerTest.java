package com.maavooripachadi.content;

import com.maavooripachadi.content.dto.BlogCard;
import com.maavooripachadi.content.dto.BlogDetail;
import com.maavooripachadi.content.dto.RecipeCard;
import com.maavooripachadi.content.dto.RecipeDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ContentControllerTest {

  private ContentService service;
  private ContentController controller;

  @BeforeEach
  void setUp() {
    service = mock(ContentService.class);
    controller = new ContentController(service);
  }

  @Test
  void recipesEndpointDelegatesToService() {
    Page<RecipeCard> cards = new PageImpl<>(List.of(new RecipeCard(1L, "Gongura", "gongura", "hero", "spicy")));
    when(service.listRecipes(1, 20, "spicy", "gongura")).thenReturn(cards);

    Page<RecipeCard> result = controller.recipes(1, 20, "spicy", "gongura");

    assertThat(result).isSameAs(cards);
    verify(service).listRecipes(1, 20, "spicy", "gongura");
  }

  @Test
  void recipeEndpointReturnsDetailFromService() {
    RecipeDetail detail = new RecipeDetail(5L, "Kodi", "kodi", "hero", "tag", "intro", "ing", "steps", "Chef");
    when(service.recipeBySlug("kodi")).thenReturn(detail);

    RecipeDetail result = controller.recipe("kodi");

    assertThat(result).isSameAs(detail);
    verify(service).recipeBySlug("kodi");
  }

  @Test
  void blogsEndpointDelegatesToService() {
    Page<BlogCard> cards = new PageImpl<>(List.of(new BlogCard(3L, "Festival", "festival", "hero", "excerpt", "tags")));
    when(service.listBlogs(0, 10, "festival", "search")).thenReturn(cards);

    Page<BlogCard> result = controller.blogs(0, 10, "festival", "search");

    assertThat(result).isSameAs(cards);
    verify(service).listBlogs(0, 10, "festival", "search");
  }

  @Test
  void blogEndpointReturnsDetailFromService() {
    BlogDetail detail = new BlogDetail(8L, "Summer", "summer", "hero", "tags", "<p>body</p>", "Team");
    when(service.blogBySlug("summer")).thenReturn(detail);

    BlogDetail result = controller.blog("summer");

    assertThat(result).isSameAs(detail);
    verify(service).blogBySlug("summer");
  }
}
