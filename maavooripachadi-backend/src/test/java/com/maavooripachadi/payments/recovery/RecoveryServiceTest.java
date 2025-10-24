package com.maavooripachadi.payments.recovery;

import com.maavooripachadi.payments.recovery.dto.RecoveryIssueRequest;
import com.maavooripachadi.payments.recovery.dto.RecoveryIssueResponse;
import com.maavooripachadi.payments.recovery.dto.RecoveryValidateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecoveryServiceTest {

    @Mock
    private RecoveryTokenRepository repository;

    private RecoveryService service;
    private RecoveryProperties properties;

    @BeforeEach
    void setUp() {
        properties = new RecoveryProperties();
        properties.setLinkBase("https://app.maavooripachadi.com");
        properties.setTtlHours(12);
        service = new RecoveryService(repository, properties);
    }

    @Test
    void issuePersistsTokenAndReturnsLink() {
        when(repository.save(any(RecoveryToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RecoveryIssueRequest request = new RecoveryIssueRequest();
        request.setOrderNo("ORD-5001");

        RecoveryIssueResponse response = service.issue(request);

        ArgumentCaptor<RecoveryToken> captor = ArgumentCaptor.forClass(RecoveryToken.class);
        verify(repository).save(captor.capture());
        RecoveryToken saved = captor.getValue();

        assertThat(saved.getOrderNo()).isEqualTo("ORD-5001");
        assertThat(saved.getToken()).isNotBlank();
        assertThat(saved.getExpiresAt()).isAfter(OffsetDateTime.now(ZoneOffset.UTC));

        assertThat(response.getToken()).isEqualTo(saved.getToken());
        assertThat(response.getUrl()).isEqualTo("https://app.maavooripachadi.com/pay/recover?token=" + saved.getToken());
    }

    @Test
    void validateReturnsOrderWhenTokenActive() {
        RecoveryToken token = new RecoveryToken();
        token.setOrderNo("ORD-6001");
        token.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusHours(2));
        when(repository.findByTokenAndConsumedFalse("valid")).thenReturn(Optional.of(token));

        RecoveryValidateResponse response = service.validate("valid");

        assertThat(response.isOk()).isTrue();
        assertThat(response.getOrderNo()).isEqualTo("ORD-6001");
    }

    @Test
    void validateReturnsNotOkWhenExpired() {
        RecoveryToken token = new RecoveryToken();
        token.setOrderNo("ORD-7001");
        token.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(5));
        when(repository.findByTokenAndConsumedFalse("expired")).thenReturn(Optional.of(token));

        RecoveryValidateResponse response = service.validate("expired");

        assertThat(response.isOk()).isFalse();
        assertThat(response.getOrderNo()).isNull();
    }

    @Test
    void consumeMarksTokenAsConsumed() {
        RecoveryToken token = new RecoveryToken();
        token.setToken("tok");
        token.setConsumed(Boolean.FALSE);
        when(repository.findByTokenAndConsumedFalse("tok")).thenReturn(Optional.of(token));
        when(repository.save(token)).thenReturn(token);

        service.consume("tok");

        assertThat(token.getConsumed()).isTrue();
        verify(repository).save(token);
    }

    @Test
    void consumeThrowsWhenTokenMissing() {
        when(repository.findByTokenAndConsumedFalse("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consume("missing")).isInstanceOf(java.util.NoSuchElementException.class);
    }
}
