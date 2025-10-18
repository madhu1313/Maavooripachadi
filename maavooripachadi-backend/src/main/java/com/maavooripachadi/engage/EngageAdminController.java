package com.maavooripachadi.engage;


import com.maavooripachadi.engage.dto.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@RestController
@RequestMapping("/api/v1/admin/engage")
@Validated
public class EngageAdminController {
    private final OutboundTemplateRepository templates;
    private final NotifyService notifyService;


    public EngageAdminController(OutboundTemplateRepository templates, NotifyService notifyService){
        this.templates = templates; this.notifyService = notifyService;
    }


    // ---- Templates CRUD ----
    @PostMapping("/templates")
    @PreAuthorize("hasAuthority('ENGAGE_WRITE') or hasRole('ADMIN')")
    public OutboundTemplate upsert(@Valid @RequestBody TemplateUpsertRequest req){
        var t = templates.findByCode(req.code()).orElse(new OutboundTemplate());
        t.setCode(req.code());
        t.setChannel(req.channel());
        t.setLocale(req.locale());
        t.setSubject(req.subject());
        t.setBodyHtml(req.bodyHtml());
        t.setBodyText(req.bodyText());
        t.setEnabled(req.enabled() == null ? Boolean.TRUE : req.enabled());
        return templates.save(t);
    }


    @GetMapping("/templates")
    @PreAuthorize("hasAuthority('ENGAGE_READ') or hasRole('ADMIN')")
    public List<OutboundTemplate> list(){ return templates.findAll(); }


    @GetMapping("/templates/{code}")
    @PreAuthorize("hasAuthority('ENGAGE_READ') or hasRole('ADMIN')")
    public OutboundTemplate get(@PathVariable String code){ return templates.findByCode(code).orElseThrow(); }


    @DeleteMapping("/templates/{code}")
    @PreAuthorize("hasAuthority('ENGAGE_WRITE') or hasRole('ADMIN')")
    public java.util.Map<String,Object> delete(@PathVariable String code){
        var t = templates.findByCode(code).orElseThrow();
        templates.delete(t);
        return java.util.Map.of("ok", true);
    }


    // ---- Preview & Send ----
    @PostMapping("/preview")
    @PreAuthorize("hasAuthority('ENGAGE_READ') or hasRole('ADMIN')")
    public java.util.Map<String, Object> preview(@Valid @RequestBody PreviewRequest req){
        return notifyService.preview(req);
    }


    @PostMapping("/send")
    @PreAuthorize("hasAuthority('ENGAGE_WRITE') or hasRole('ADMIN')")
    public java.util.Map<String, Object> send(@Valid @RequestBody SendRequest req){
        Long id = notifyService.send(req);
        return java.util.Map.of("ok", true, "sendId", id);
    }
}