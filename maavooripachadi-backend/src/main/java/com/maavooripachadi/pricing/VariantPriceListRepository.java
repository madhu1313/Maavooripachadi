package com.maavooripachadi.pricing;
import org.springframework.data.jpa.repository.*;

public interface VariantPriceListRepository extends JpaRepository<VariantPriceList,Long>{ java.util.List<VariantPriceList> findByListCode(String code); }
