package com.maavooripachadi.i18n.dto;


import jakarta.validation.constraints.NotBlank;


public record I18nDeleteRequest(
        @NotBlank String namespace,
        @NotBlank String key,
        @NotBlank String locale
) {}