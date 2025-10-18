package com.maavooripachadi.catalog;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);


    @Query("""
SELECT p FROM Product p
WHERE (:category IS NULL OR p.categorySlug = :category)
AND (:tag IS NULL OR p.tags LIKE CONCAT('%', :tag, '%'))
AND (
    :q IS NULL
    OR lower(p.title) LIKE CONCAT('%', lower(:q), '%')
    OR lower(cast(coalesce(p.searchText, '') as string)) LIKE CONCAT('%', lower(:q), '%')
)
AND (:minP IS NULL OR p.pricePaise >= :minP)
AND (:maxP IS NULL OR p.pricePaise <= :maxP)
""")
    Page<Product> search(@Param("category") String category,
                         @Param("tag") String tag,
                         @Param("q") String q,
                         @Param("minP") Integer minP,
                         @Param("maxP") Integer maxP,
                         Pageable pageable);


    List<Product> findTopByTitleContainingIgnoreCaseOrTagsContainingIgnoreCase(String a, String b, Pageable pageable);
}
