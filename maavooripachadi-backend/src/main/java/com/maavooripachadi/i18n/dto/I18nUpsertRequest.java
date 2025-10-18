package com.maavooripachadi.i18n.dto;


import jakarta.validation.constraints.NotBlank;


public record I18nUpsertRequest(
        @NotBlank String namespace,
        @NotBlank String key,
        @NotBlank String locale,
        @NotBlank String text,
        String tags,
        Boolean approved
) {}