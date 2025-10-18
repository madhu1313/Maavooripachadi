package com.maavooripachadi.content;


import com.maavooripachadi.content.dto.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class ContentController {
  private final ContentService content;


  @GetMapping("/recipes")
  public Page<RecipeCard> recipes(@RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
                                  @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(60) int size,
                                  @RequestParam(value = "tag", required = false) @Size(min=1, max=64) String tag,
                                  @RequestParam(value = "q", required = false) @Size(min=1, max=64) String q){
    return content.listRecipes(page, size, tag, q);
  }


  @GetMapping("/recipes/{slug}")
  public RecipeDetail recipe(@PathVariable String slug){
    return content.recipeBySlug(slug);
  }


  @GetMapping("/blog")
  public Page<BlogCard> blogs(@RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
                              @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(60) int size,
                              @RequestParam(value = "tag", required = false) @Size(min=1, max=64) String tag,
                              @RequestParam(value = "q", required = false) @Size(min=1, max=64) String q){
    return content.listBlogs(page, size, tag, q);
  }


  @GetMapping("/blog/{slug}")
  public BlogDetail blog(@PathVariable String slug){
    return content.blogBySlug(slug);
  }
}
