package com.maavooripachadi.privacy;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PrivacyPolicyRepository extends JpaRepository<PrivacyPolicy, Long> {
    Optional<PrivacyPolicy> findFirstByActiveTrueOrderByCreatedAtDesc();
    Optional<PrivacyPolicy> findByPolicyVersion(String version);
}
