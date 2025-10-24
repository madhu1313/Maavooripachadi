package com.maavooripachadi.support;

import com.maavooripachadi.support.dto.CannedUpsertRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CannedResponseServiceTest {

    @Mock private CannedResponseRepository repository;

    private CannedResponseService service;

    @BeforeEach
    void setUp() {
        service = new CannedResponseService(repository);
    }

    @Test
    void upsertCreatesNewResponseWithDefaultLocale() {
        CannedUpsertRequest request = new CannedUpsertRequest();
        request.setKeyName("greeting");
        request.setBody("Hello there");

        when(repository.findByKeyName("greeting")).thenReturn(Optional.empty());
        when(repository.save(any(CannedResponse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CannedResponse response = service.upsert(request);

        assertThat(response.getKeyName()).isEqualTo("greeting");
        assertThat(response.getBody()).isEqualTo("Hello there");
        assertThat(response.getLocale()).isEqualTo("en-IN");
    }

    @Test
    void upsertUpdatesExistingResponse() {
        CannedResponse existing = new CannedResponse();
        existing.setKeyName("thanks");
        existing.setLocale("en-IN");
        when(repository.findByKeyName("thanks")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        CannedUpsertRequest request = new CannedUpsertRequest();
        request.setKeyName("thanks");
        request.setBody("Thank you!");
        request.setLocale("en-US");

        CannedResponse response = service.upsert(request);

        assertThat(response.getBody()).isEqualTo("Thank you!");
        assertThat(response.getLocale()).isEqualTo("en-US");
    }
}
