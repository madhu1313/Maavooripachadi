package com.maavooripachadi.support;

import com.maavooripachadi.support.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportMessageRepository extends JpaRepository<SupportMessage,Long> {
    java.util.List<SupportMessage> findByTicketId(Long id); }