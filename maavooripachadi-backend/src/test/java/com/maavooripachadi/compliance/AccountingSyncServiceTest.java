package com.maavooripachadi.compliance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AccountingSyncServiceTest {

    private AccountingSyncRepository repository;
    private AccountingSyncService service;

    @BeforeEach
    void setUp() {
        repository = mock(AccountingSyncRepository.class);
        service = new AccountingSyncService(repository);
    }

    @Test
    void queueCreatesQueuedRecord() {
        when(repository.save(any(AccountingSync.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountingSync result = service.queue("ORDER", "ORD-10", "ZOHO");

        assertThat(result.getType()).isEqualTo("ORDER");
        assertThat(result.getRefId()).isEqualTo("ORD-10");
        assertThat(result.getSystem()).isEqualTo("ZOHO");
        assertThat(result.getStatus()).isEqualTo("QUEUED");
    }
}
