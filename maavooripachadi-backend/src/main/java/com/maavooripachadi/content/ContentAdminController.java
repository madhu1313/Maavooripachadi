package com.maavooripachadi.content;


import com.maavooripachadi.content.dto.SubmitContentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/content")
@RequiredArgsConstructor
@Validated
public class ContentAdminController {
    private final ContentApprovalService approvals;
    private final ContentService content;


    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAuthority('CONTENT_SUBMIT') or hasRole('ADMIN')")
    public java.util.Map<String,Object> submit(@Valid @RequestBody SubmitContentRequest req,
                                               @RequestHeader(value = "X-Requestor", required = false) String actor){
        var a = approvals.submit(req.type(), Long.parseLong(req.id()), actor == null ? "system" : actor);
        return java.util.Map.of("approvalId", a.getId(), "status", a.getStatus());
    }


    @PostMapping("/{approvalId}/decide")
    @PreAuthorize("hasAuthority('CONTENT_APPROVE') or hasRole('ADMIN')")
    public java.util.Map<String,Object> decide(@PathVariable long approvalId,
                                               @RequestParam(value = "decision") @NotBlank String decision,
                                               @RequestParam(value = "note", required=false) String note,
                                               @RequestHeader(value = "X-Requestor", required = false) String actor){
        var a = approvals.decide(approvalId, decision, actor == null ? "system" : actor, note);
        return java.util.Map.of("approvalId", a.getId(), "status", a.getStatus());
    }


    @PostMapping("/{type}/{id}/publish")
    @PreAuthorize("hasAuthority('CONTENT_PUBLISH') or hasRole('ADMIN')")
    public java.util.Map<String,Object> publish(@PathVariable String type, @PathVariable long id){
        content.publish(type, id); return java.util.Map.of("ok", true);
    }


    @PostMapping("/{type}/{id}/unpublish")
    @PreAuthorize("hasAuthority('CONTENT_PUBLISH') or hasRole('ADMIN')")
    public java.util.Map<String,Object> unpublish(@PathVariable String type, @PathVariable long id){
        content.unpublish(type, id); return java.util.Map.of("ok", true);
    }
}
