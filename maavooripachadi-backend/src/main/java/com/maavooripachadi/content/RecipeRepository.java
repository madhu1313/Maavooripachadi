package com.maavooripachadi.content;


import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query;
import java.util.Optional;


public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findBySlugAndPublishedTrue(String slug);


    @Query("""
SELECT r FROM Recipe r
WHERE r.published = true
AND (:tag IS NULL OR r.tags LIKE CONCAT('%', :tag, '%'))
AND (
    :q IS NULL
    OR lower(r.title) LIKE CONCAT('%', lower(:q), '%')
    OR lower(cast(coalesce(r.introHtml, '') as string)) LIKE CONCAT('%', lower(:q), '%')
)
""")
    Page<Recipe> search(String tag, String q, Pageable pageable);
}
