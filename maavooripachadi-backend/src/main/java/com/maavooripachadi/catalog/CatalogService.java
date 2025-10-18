package com.maavooripachadi.catalog;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;


@Service
@RequiredArgsConstructor
public class CatalogService {
    private final ProductRepository products;
    private final VariantRepository variants;


    public Page<ProductCard> list(int page, int size, String sort, String category, String tag, String q, Integer minP, Integer maxP){
        Sort s = parseSort(sort);
        Pageable p = PageRequest.of(page, size, s);
        Page<Product> raw = products.search(category, tag, q, minP, maxP, p);
        return raw.map(ProductCard::from);
    }


    public ProductDetail detail(String slug){
        Product prod = products.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + slug));
        List<Variant> vs = variants.findByProductId(prod.getId());
        return ProductDetail.from(prod, vs);
    }


    public List<ProductSuggestion> suggest(String q, int limit){
        return products.findTopByTitleContainingIgnoreCaseOrTagsContainingIgnoreCase(q, q, PageRequest.of(0, limit))
                .stream().map(ProductSuggestion::from).toList();
    }


    private Sort parseSort(String sort){
        try {
            String[] parts = sort.split(",");
            String field = parts[0];
            Sort.Direction dir = parts.length>1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            return Sort.by(dir, switch (field){
                case "price", "pricePaise" -> "pricePaise";
                case "title" -> "title";
                default -> "createdAt";
            });
        } catch (Exception e){
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }
}
