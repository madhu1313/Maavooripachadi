package com.maavooripachadi.support;

import com.maavooripachadi.support.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SupportService {
  private final SupportTicketRepository tickets; private final TicketMessageRepository messages; private final TicketAttachmentRepository attachments; private final TicketEventRepository events; private final SlaService sla; private final CannedResponseRepository canned; private final CsatSurveyRepository csat;

  public SupportService(SupportTicketRepository tickets, TicketMessageRepository messages, TicketAttachmentRepository attachments, TicketEventRepository events, SlaService sla, CannedResponseRepository canned, CsatSurveyRepository csat){
    this.tickets=tickets; this.messages=messages; this.attachments=attachments; this.events=events; this.sla=sla; this.canned=canned; this.csat=csat;
  }

  @Transactional
  public SupportTicket open(CreateTicketRequest req){
    SupportTicket t = new SupportTicket();
    t.setTicketNo(generateTicketNo()); t.setSubject(req.getSubject()); t.setDescription(req.getDescription()); t.setRequesterEmail(req.getRequesterEmail()); t.setRequesterName(req.getRequesterName()); t.setPriority(req.getPriority()); t.setChannel(req.getChannel()); t.setStatus(TicketStatus.OPEN);
    sla.applyDefaults(t);
    t = tickets.save(t);
    log(t, "CREATED", null);
    return t;
  }

  @Transactional
  public String open(String subject, Long userId, String orderNo, String body){
    CreateTicketRequest req = new CreateTicketRequest();
    req.setSubject(subject);
    var description = new StringBuilder(body == null ? "" : body);
    if (orderNo != null && !orderNo.isBlank()) description.append("\n\nOrder: ").append(orderNo);
    if (userId != null) description.append("\nUser ID: ").append(userId);
    var descriptionText = description.toString();
    if (descriptionText.isBlank()) descriptionText = subject;
    req.setDescription(descriptionText);
    req.setChannel(TicketChannel.WEB);
    return open(req).getTicketNo();
  }

  @Transactional
  public TicketMessage addMessage(AddMessageRequest req){
    SupportTicket t = tickets.findByTicketNo(req.getTicketNo()).orElseThrow();
    TicketMessage m = new TicketMessage(); m.setTicket(t); m.setAuthor(req.getAuthor()); m.setBody(req.getBody()); m.setVisibility(req.getVisibility());
    m = messages.save(m);
    if (m.getVisibility()==MessageVisibility.PUBLIC){ t.setStatus(TicketStatus.PENDING_AGENT); tickets.save(t); }
    log(t, "MESSAGE", null);
    return m;
  }

  @Transactional(readOnly = true)
  public Map<String,Object> view(String ticketNo){
    SupportTicket t = tickets.findByTicketNo(ticketNo).orElseThrow();
    List<TicketMessage> msgs = messages.findByTicketIdOrderByCreatedAtAsc(t.getId());
    return Map.of("ticket", t, "messages", msgs);
  }

  @Transactional
  public Map<String,Object> reply(String ticketNo, String author, String body){
    AddMessageRequest req = new AddMessageRequest();
    req.setTicketNo(ticketNo);
    req.setAuthor(author);
    req.setBody(body);
    TicketMessage msg = addMessage(req);
    return Map.of("ticketNo", ticketNo, "messageId", msg.getId());
  }

  @Transactional(readOnly = true)
  public Page<SupportTicket> list(TicketStatus status, int page, int size){ return tickets.findByStatusOrderByCreatedAtDesc(status, PageRequest.of(page, size)); }

  @Transactional
  public SupportTicket assign(AssignAgentRequest req){
    SupportTicket t = tickets.findByTicketNo(req.getTicketNo()).orElseThrow();
    t.setAssignee(req.getAgent()); t = tickets.save(t); log(t, "ASSIGNED", req.getAgent()); return t;
  }

  @Transactional
  public SupportTicket updateStatus(UpdateStatusRequest req){
    SupportTicket t = tickets.findByTicketNo(req.getTicketNo()).orElseThrow();
    t.setStatus(req.getStatus());
    if (req.getStatus()==TicketStatus.RESOLVED || req.getStatus()==TicketStatus.CLOSED) t.setClosedAt(OffsetDateTime.now());
    t = tickets.save(t);
    log(t, "STATUS", req.getNote());
    return t;
  }

  @Transactional
  public SupportTicket addTag(AddTagRequest req){
    SupportTicket t = tickets.findByTicketNo(req.getTicketNo()).orElseThrow();
    var tags = t.getTags(); tags.add(req.getTag()); t.setTags(tags);
    t = tickets.save(t); log(t, "TAG_ADDED", req.getTag()); return t;
  }

  @Transactional
  public CsatSurvey submitCsat(CsatSubmitRequest req){
    SupportTicket t = tickets.findByTicketNo(req.getTicketNo()).orElseThrow();
    CsatSurvey s = new CsatSurvey(); s.setTicket(t); s.setRating(req.getRating()); s.setComment(req.getComment());
    return csat.save(s);
  }

  private String generateTicketNo(){
    String suffix = UUID.randomUUID().toString().substring(0,8).toUpperCase();
    return "ST-"+java.time.LocalDate.now()+"-"+suffix;
  }

  private void log(SupportTicket t, String kind, String payload){
    TicketEvent e = new TicketEvent(); e.setTicket(t); e.setKind(kind); e.setPayloadJson(payload);
    events.save(e);
  }
}
