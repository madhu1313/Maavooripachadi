package com.maavooripachadi.i18n;


import com.maavooripachadi.i18n.dto.I18nBundleResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/i18n")
@Validated
public class I18nController {
    private final I18nService service;


    public I18nController(I18nService service) { this.service = service; }


    /**
     * Example: GET /api/v1/i18n/bundle?ns=storefront&locale=en-IN
     */
    @GetMapping("/bundle")
    public I18nBundleResponse bundle(@RequestParam("ns") @NotBlank String namespace,
                                     @RequestParam("locale") @NotBlank String locale){
        var map = service.getBundle(namespace, locale);
        return new I18nBundleResponse(namespace, locale, map);
    }
}