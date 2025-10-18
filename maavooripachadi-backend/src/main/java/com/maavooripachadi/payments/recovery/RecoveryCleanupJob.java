package com.maavooripachadi.payments.recovery;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;


@Component
public class RecoveryCleanupJob {


    private final RecoveryTokenRepository repo;


    public RecoveryCleanupJob(RecoveryTokenRepository repo){ this.repo = repo; }


    /** Purge expired, unconsumed tokens every hour. */
    @Scheduled(cron = "0 5 * * * *")
    @Transactional
    public void purge(){
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<RecoveryToken> list = repo.findByConsumedFalseAndExpiresAtBefore(now);
        if (!list.isEmpty()) repo.deleteAllInBatch(list);
    }
}