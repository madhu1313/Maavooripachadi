package com.maavooripachadi.privacy;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, Long> {
    List<ConsentRecord> findBySubjectIdOrderByCreatedAtDesc(String subjectId);
    List<ConsentRecord> findBySessionIdOrderByCreatedAtDesc(String sessionId);
}