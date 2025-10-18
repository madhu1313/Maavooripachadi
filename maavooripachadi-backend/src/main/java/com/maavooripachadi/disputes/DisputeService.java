package com.maavooripachadi.disputes;


import com.maavooripachadi.disputes.dto.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.List;


@Service
public class DisputeService {

private final DisputeRepository disputes;
private final DisputeEventRepository events;


public DisputeService(DisputeRepository disputes, DisputeEventRepository events) {
  this.disputes = disputes; this.events = events;
}


@Transactional
public Dispute create(CreateDisputeRequest req){
  var existing = disputes.findByProviderCaseId(req.providerCaseId());
  if (existing.isPresent()) return existing.get();


  var d = new Dispute();
  d.setGateway(req.gateway().toUpperCase());
  d.setProviderCaseId(req.providerCaseId());
  d.setOrderNo(req.orderNo());
  d.setPaymentAttemptId(req.paymentAttemptId());
  d.setReason(req.reason());
  d.setType(req.type());
  d.setAmountPaise(req.amountPaise());
  d.setCurrency(req.currency() == null ? "INR" : req.currency().toUpperCase());
  d.setStatus(DisputeStatus.OPEN);
  var saved = disputes.save(d);


  var ev = new DisputeEvent();
  ev.setDispute(saved);
  ev.setType(DisputeEventType.OPENED);
  ev.setPayload("dispute opened");
  events.save(ev);


  return saved;
}


@Transactional
public Dispute updateStatus(long id, DisputeStatus status, String note){
  var d = disputes.findById(id).orElseThrow();
  d.setStatus(status);
  if (status == DisputeStatus.WON || status == DisputeStatus.LOST || status == DisputeStatus.CLOSED) {
    d.setDecidedAt(OffsetDateTime.now());
  }
  if (note != null && !note.isBlank()) {
    var ev = new DisputeEvent();
    ev.setDispute(d); ev.setType(DisputeEventType.STATUS_CHANGE); ev.setPayload(note);
    events.save(ev);
  }
  return disputes.save(d);
}


@Transactional
public Dispute addNote(long id, String note){
  var d = disputes.findById(id).orElseThrow();
  var ev = new DisputeEvent();
  ev.setDispute(d); ev.setType(DisputeEventType.NOTE); ev.setPayload(note);
  events.save(ev);
  String cur = d.getNotes();
  d.setNotes((cur == null || cur.isBlank() ? note : cur + "\n" + note));
  return disputes.save(d);
}


@Transactional(readOnly = true)
public Page<Dispute> list(String gateway, DisputeStatus status, String q, int page, int size){
  return disputes.search(gateway, status, q, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
}


@Transactional(readOnly = true)
public List<DisputeEvent> timeline(long id){
  return events.findByDisputeIdOrderByCreatedAtAsc(id);
}
}