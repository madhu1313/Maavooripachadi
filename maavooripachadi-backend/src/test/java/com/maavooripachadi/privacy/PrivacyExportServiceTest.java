package com.maavooripachadi.privacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PrivacyExportServiceTest {

  private ConsentRecordRepository consentRepository;
  private PrivacyExportService service;

  @BeforeEach
  void setUp() {
    consentRepository = mock(ConsentRecordRepository.class);
    service = new PrivacyExportService(consentRepository);
  }

  @Test
  void exportConsentsCsvFormatsRecordsWithHeader() {
    ConsentRecord record = new ConsentRecord();
    record.setCategory(ConsentCategory.MARKETING);
    record.setStatus(ConsentStatus.GRANTED);
    record.setPolicyVersion("v1");
    record.setSource("SETTINGS");
    ReflectionTestUtils.setField(record, "createdAt", OffsetDateTime.parse("2025-01-01T10:15:30Z"));

    when(consentRepository.findBySubjectIdOrderByCreatedAtDesc("user-1"))
        .thenReturn(List.of(record));

    byte[] csvBytes = service.exportConsentsCsv("user-1");
    String csv = new String(csvBytes, StandardCharsets.UTF_8);

    assertThat(csv).startsWith("createdAt,category,status,policyVersion,source\n");
    assertThat(csv).contains("2025-01-01T10:15:30").contains("MARKETING").contains("GRANTED").contains("v1").contains("SETTINGS");
  }
}
