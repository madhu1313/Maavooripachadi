package com.maavooripachadi.compliance;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface Gstr1SummaryRepository extends JpaRepository<Gstr1Summary, Long> {
    Optional<Gstr1Summary> findByPeriod(String period);
}