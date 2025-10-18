package com.maavooripachadi.privacy;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.jdbc.core.JdbcTemplate;
@Component @RequiredArgsConstructor public class RetentionJob {
    private final JdbcTemplate jdbc; @Scheduled(cron="0 0 3 * * *")
    public void run(){ /* iterate policies and mask/delete */ } }
