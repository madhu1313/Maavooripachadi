package com.maavooripachadi.engage;

import com.maavooripachadi.engage.dto.PreviewRequest;
import com.maavooripachadi.engage.dto.SendRequest;
import com.maavooripachadi.engage.dto.TemplateUpsertRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EngageAdminControllerTest {

  private OutboundTemplateRepository templateRepository;
  private NotifyService notifyService;
  private EngageAdminController controller;

  @BeforeEach
  void setUp() {
    templateRepository = mock(OutboundTemplateRepository.class);
    notifyService = mock(NotifyService.class);
    controller = new EngageAdminController(templateRepository, notifyService);
  }

  @Test
  void upsertCreatesNewTemplateWhenAbsent() {
    TemplateUpsertRequest request = new TemplateUpsertRequest(
        "ORDER_CONF",
        OutboundChannel.EMAIL,
        "en_IN",
        "Subject",
        "<p>Body</p>",
        "Body",
        true);

    when(templateRepository.findByCode("ORDER_CONF")).thenReturn(Optional.empty());
    when(templateRepository.save(any(OutboundTemplate.class))).thenAnswer(invocation -> invocation.getArgument(0));

    OutboundTemplate saved = controller.upsert(request);

    assertThat(saved.getCode()).isEqualTo("ORDER_CONF");
    assertThat(saved.getChannel()).isEqualTo(OutboundChannel.EMAIL);
    assertThat(saved.getLocale()).isEqualTo("en_IN");
    assertThat(saved.getSubject()).isEqualTo("Subject");
    assertThat(saved.getBodyHtml()).isEqualTo("<p>Body</p>");
    assertThat(saved.getBodyText()).isEqualTo("Body");
    assertThat(saved.getEnabled()).isTrue();
    verify(templateRepository).save(saved);
  }

  @Test
  void upsertUpdatesExistingTemplate() {
    OutboundTemplate existing = new OutboundTemplate();
    existing.setCode("ORDER_CONF");
    existing.setChannel(OutboundChannel.EMAIL);
    existing.setSubject("Old");

    TemplateUpsertRequest request = new TemplateUpsertRequest(
        "ORDER_CONF",
        OutboundChannel.PUSH,
        "en_US",
        "New subject",
        null,
        "Push body",
        false);

    when(templateRepository.findByCode("ORDER_CONF")).thenReturn(Optional.of(existing));
    when(templateRepository.save(existing)).thenReturn(existing);

    OutboundTemplate saved = controller.upsert(request);

    assertThat(saved).isSameAs(existing);
    assertThat(saved.getChannel()).isEqualTo(OutboundChannel.PUSH);
    assertThat(saved.getLocale()).isEqualTo("en_US");
    assertThat(saved.getSubject()).isEqualTo("New subject");
    assertThat(saved.getBodyText()).isEqualTo("Push body");
    assertThat(saved.getEnabled()).isFalse();
  }

  @Test
  void listReturnsAllTemplates() {
    List<OutboundTemplate> templates = List.of(new OutboundTemplate());
    when(templateRepository.findAll()).thenReturn(templates);

    assertThat(controller.list()).isEqualTo(templates);
  }

  @Test
  void getReturnsTemplateByCode() {
    OutboundTemplate template = new OutboundTemplate();
    when(templateRepository.findByCode("ORDER_CONF")).thenReturn(Optional.of(template));

    assertThat(controller.get("ORDER_CONF")).isSameAs(template);
  }

  @Test
  void deleteRemovesTemplateAndReturnsOk() {
    OutboundTemplate template = new OutboundTemplate();
    when(templateRepository.findByCode("ORDER_CONF")).thenReturn(Optional.of(template));

    Map<String, Object> response = controller.delete("ORDER_CONF");

    assertThat(response).containsEntry("ok", true);
    verify(templateRepository).delete(template);
  }

  @Test
  void previewDelegatesToNotifyService() {
    PreviewRequest request = new PreviewRequest("ORDER_CONF", Map.of("name", "Sara"));
    when(notifyService.preview(request)).thenReturn(Map.of("subject", "Hi Sara"));

    Map<String, Object> preview = controller.preview(request);

    assertThat(preview).containsEntry("subject", "Hi Sara");
    verify(notifyService).preview(request);
  }

  @Test
  void sendDelegatesToNotifyService() {
    SendRequest request = new SendRequest(OutboundChannel.EMAIL, "ORDER_CONF", "user@example.com", Map.of());
    when(notifyService.send(request)).thenReturn(55L);

    Map<String, Object> response = controller.send(request);

    assertThat(response).containsEntry("ok", true).containsEntry("sendId", 55L);
    verify(notifyService).send(request);
  }
}
