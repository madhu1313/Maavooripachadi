package com.maavooripachadi.i18n;


import com.maavooripachadi.i18n.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;


@RestController
@RequestMapping("/api/v1/admin/i18n")
@Validated
public class I18nAdminController {
    private final I18nService service;
    private final I18nStringRepository repo;


    public I18nAdminController(I18nService service, I18nStringRepository repo) {
        this.service = service; this.repo = repo;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('I18N_WRITE') or hasRole('ADMIN')")
    public I18nString upsert(@Valid @RequestBody I18nUpsertRequest req){
        return service.upsert(req);
    }


    @DeleteMapping
    @PreAuthorize("hasAuthority('I18N_WRITE') or hasRole('ADMIN')")
    public java.util.Map<String,Object> delete(@Valid @RequestBody I18nDeleteRequest req){
        service.delete(req.namespace(), req.key(), req.locale());
        return java.util.Map.of("ok", true);
    }


    @PostMapping("/import")
    @PreAuthorize("hasAuthority('I18N_WRITE') or hasRole('ADMIN')")
    public java.util.Map<String,Object> importCsv(@Valid @RequestBody I18nImportCsvRequest req) throws IOException {
        int n = service.importCsv(req.csv());
        return java.util.Map.of("imported", n);
    }


    @GetMapping("/export")
    @PreAuthorize("hasAuthority('I18N_READ') or hasRole('ADMIN')")
    public String export(@RequestParam(value = "ns", required = false) String ns,
                         @RequestParam(value = "locale", required = false) String locale){
        return service.exportCsv(ns, locale);
    }
}
