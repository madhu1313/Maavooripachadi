package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.DenyListUpsertRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DenyListServiceTest {

    @Mock
    private DenyListRepository repository;

    private DenyListService service;

    @BeforeEach
    void setUp() {
        service = new DenyListService(repository);
    }

    @Test
    void upsertUpdatesExistingEntry() {
        DenyListUpsertRequest request = new DenyListUpsertRequest();
        request.setType(DenyType.EMAIL);
        request.setValue("blocked@example.com");
        request.setReason("chargeback");
        request.setSource("admin");
        request.setExpiresAt("2030-01-01T00:00:00Z");

        DenyListEntry existing = new DenyListEntry();
        when(repository.findByTypeAndValue(DenyType.EMAIL, "blocked@example.com")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        DenyListEntry result = service.upsert(request);

        assertThat(result.getReason()).isEqualTo("chargeback");
        assertThat(result.getSource()).isEqualTo("admin");
        assertThat(result.getExpiresAt()).isEqualTo(OffsetDateTime.parse("2030-01-01T00:00:00Z"));
    }

    @Test
    void isDeniedConsidersExpiration() {
        DenyListEntry active = new DenyListEntry();
        active.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(10));
        when(repository.findByTypeAndValue(DenyType.IP, "1.2.3.4")).thenReturn(Optional.of(active));

        assertThat(service.isDenied(DenyType.IP, "1.2.3.4")).isTrue();

        DenyListEntry expired = new DenyListEntry();
        expired.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(5));
        when(repository.findByTypeAndValue(DenyType.IP, "1.2.3.4")).thenReturn(Optional.of(expired));

        assertThat(service.isDenied(DenyType.IP, "1.2.3.4")).isFalse();
    }
}
