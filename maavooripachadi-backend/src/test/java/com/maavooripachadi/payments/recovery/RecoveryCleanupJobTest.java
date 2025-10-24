package com.maavooripachadi.payments.recovery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecoveryCleanupJobTest {

    @Mock
    private RecoveryTokenRepository repository;

    private RecoveryCleanupJob job;

    @BeforeEach
    void setUp() {
        job = new RecoveryCleanupJob(repository);
    }

    @Test
    void purgeDeletesExpiredTokens() {
        RecoveryToken token = new RecoveryToken();
        token.setToken("tok");
        token.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1));
        when(repository.findByConsumedFalseAndExpiresAtBefore(any())).thenReturn(List.of(token));

        job.purge();

        verify(repository).deleteAllInBatch(List.of(token));
    }
}
