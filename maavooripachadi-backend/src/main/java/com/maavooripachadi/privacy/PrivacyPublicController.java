package com.maavooripachadi.privacy;


import com.maavooripachadi.privacy.dto.ConsentUpsertRequest;
import com.maavooripachadi.privacy.dto.CookiePrefRequest;
import com.maavooripachadi.privacy.dto.DsrOpenRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/privacy")
@Validated
public class PrivacyPublicController {
    private final PolicyService policies; private final ConsentService consent; private final CookieService cookies; private final DsrService dsr; private final PrivacyExportService export;
    public PrivacyPublicController(PolicyService p, ConsentService c, CookieService k, DsrService d, PrivacyExportService e){ this.policies=p; this.consent=c; this.cookies=k; this.dsr=d; this.export=e; }


    @GetMapping("/policy")
    public PrivacyPolicy activePolicy(){ return policies.getActive(); }


    @PostMapping("/consent")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsentRecord upsertConsent(@Valid @RequestBody ConsentUpsertRequest req){ return consent.upsert(req); }


    @PostMapping("/cookies")
    public CookiePreference setCookies(@Valid @RequestBody CookiePrefRequest req){ return cookies.set(req); }


    @PostMapping("/dsr")
    @ResponseStatus(HttpStatus.CREATED)
    public DsrRequest openDsr(@Valid @RequestBody DsrOpenRequest req){ return dsr.open(req); }


    @GetMapping(value = "/consents/export")
    public ResponseEntity<byte[]> exportConsents(@RequestParam String subjectId){
        byte[] csv = export.exportConsentsCsv(subjectId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=consents-"+subjectId+".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}