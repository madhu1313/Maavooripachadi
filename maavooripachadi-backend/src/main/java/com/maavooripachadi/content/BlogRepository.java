package com.maavooripachadi.content;


import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Query;
import java.util.Optional;


public interface BlogRepository extends JpaRepository<BlogPost, Long> {
    Optional<BlogPost> findBySlugAndPublishedTrue(String slug);


    @Query("""
SELECT b FROM BlogPost b
WHERE b.published = true
AND (:tag IS NULL OR b.tags LIKE CONCAT('%', :tag, '%'))
AND (
    :q IS NULL
    OR lower(b.title) LIKE CONCAT('%', lower(:q), '%')
    OR lower(cast(coalesce(b.bodyHtml, '') as string)) LIKE CONCAT('%', lower(:q), '%')
)
""")
    Page<BlogPost> search(String tag, String q, Pageable pageable);
}
