package com.maavooripachadi.engage;

import com.maavooripachadi.engage.dto.PreviewRequest;
import com.maavooripachadi.engage.dto.SendRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotifyServiceTest {

  private OutboundTemplateRepository templateRepository;
  private OutboundSendLogRepository logRepository;
  private JavaMailSender mailSender;
  private NotifyService service;
  private OutboundTemplate template;

  @BeforeEach
  void setUp() {
    templateRepository = mock(OutboundTemplateRepository.class);
    logRepository = mock(OutboundSendLogRepository.class);
    mailSender = mock(JavaMailSender.class);
    service = new NotifyService(templateRepository, logRepository, mailSender);

    template = new OutboundTemplate();
    template.setCode("ORDER_CONF");
    template.setChannel(OutboundChannel.EMAIL);
    template.setSubject("Hello {{name}}");
    template.setBodyHtml("<p>Order {{order}}</p>");
    template.setBodyText("Order {{order}}");
    template.setEnabled(true);
  }

  @Test
  void previewRendersTemplatePlaceholders() {
    when(templateRepository.findByCode("ORDER_CONF")).thenReturn(Optional.of(template));

    Map<String, Object> preview = service.preview(new PreviewRequest("ORDER_CONF",
        Map.of("name", "Arjun", "order", "ORD-9")));

    assertThat(preview.get("subject")).isEqualTo("Hello Arjun");
    assertThat(preview.get("bodyHtml")).isEqualTo("<p>Order ORD-9</p>");
    assertThat(preview.get("bodyText")).isEqualTo("Order ORD-9");
  }

  @Test
  void sendEmailLogsSuccessfulDispatch() {
    when(templateRepository.findByCode("ORDER_CONF")).thenReturn(Optional.of(template));
    when(logRepository.save(any(OutboundSendLog.class))).thenAnswer(invocation -> {
      OutboundSendLog log = invocation.getArgument(0);
      ReflectionTestUtils.setField(log, "id", 101L);
      return log;
    });

    Long sendId = service.send(new SendRequest(
        OutboundChannel.EMAIL,
        "ORDER_CONF",
        "user@example.com",
        Map.of("name", "Ravi", "order", "ORD-1")));

    assertThat(sendId).isEqualTo(101L);

    ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    SimpleMailMessage message = messageCaptor.getValue();
    assertThat(message.getTo()).containsExactly("user@example.com");
    assertThat(message.getSubject()).isEqualTo("Hello Ravi");
    assertThat(message.getText()).contains("ORD-1");

    ArgumentCaptor<OutboundSendLog> logCaptor = ArgumentCaptor.forClass(OutboundSendLog.class);
    verify(logRepository).save(logCaptor.capture());
    OutboundSendLog log = logCaptor.getValue();
    assertThat(log.getStatus()).isEqualTo("SENT");
    assertThat(log.getProviderMessageId()).startsWith("mail-");
    assertThat(log.getError()).isNull();
  }

  @Test
  void sendThrowsWhenTemplateDisabled() {
    template.setEnabled(false);
    when(templateRepository.findByCode("ORDER_CONF")).thenReturn(Optional.of(template));

    assertThatThrownBy(() -> service.send(new SendRequest(
        OutboundChannel.EMAIL,
        "ORDER_CONF",
        "user@example.com",
        Map.of())))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Template disabled");

    verifyNoInteractions(mailSender);
    verify(logRepository, never()).save(any());
  }

  @Test
  void sendLogsFailureWhenEmailProviderThrows() {
    when(templateRepository.findByCode("ORDER_CONF")).thenReturn(Optional.of(template));
    doThrow(new RuntimeException("SMTP down")).when(mailSender).send(any(SimpleMailMessage.class));
    when(logRepository.save(any(OutboundSendLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

    assertThatThrownBy(() -> service.send(new SendRequest(
        OutboundChannel.EMAIL,
        "ORDER_CONF",
        "user@example.com",
        Map.of())))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Send failed");

    ArgumentCaptor<OutboundSendLog> logCaptor = ArgumentCaptor.forClass(OutboundSendLog.class);
    verify(logRepository).save(logCaptor.capture());
    OutboundSendLog log = logCaptor.getValue();
    assertThat(log.getStatus()).isEqualTo("FAILED");
    assertThat(log.getError()).contains("SMTP down");
  }
}
