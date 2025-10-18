package com.maavooripachadi.privacy;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface CookiePreferenceRepository extends JpaRepository<CookiePreference, Long> {
    Optional<CookiePreference> findFirstBySubjectIdOrSessionIdOrderByCreatedAtDesc(String subjectId, String sessionId);
}