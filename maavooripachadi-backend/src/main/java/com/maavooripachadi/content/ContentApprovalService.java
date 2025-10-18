package com.maavooripachadi.content;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ContentApprovalService {
  private final ContentApprovalRepository repo;
  private final ContentService content;


  @Transactional
  public ContentApproval submit(String type, long id, String actor){
    var a = new ContentApproval();
    a.setType(type.toUpperCase());
    a.setRefId(id);
    a.setSubmittedBy(actor);
    a.setStatus("PENDING");
    return repo.save(a);
  }


  @Transactional
  public ContentApproval decide(long approvalId, String decision, String actor, String note){
    var a = repo.findById(approvalId).orElseThrow();
    var dec = decision.toUpperCase();
    a.setDecidedBy(actor);
    a.setStatus(dec);
    a.setNote(note);
    repo.save(a);
    if ("APPROVED".equals(dec)) {
      content.publish(a.getType(), a.getRefId());
    } else if ("REJECTED".equals(dec)) {
      content.unpublish(a.getType(), a.getRefId());
    }
    return a;
  }
}