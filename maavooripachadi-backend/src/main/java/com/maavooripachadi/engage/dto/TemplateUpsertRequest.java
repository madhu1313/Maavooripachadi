package com.maavooripachadi.engage.dto;


import com.maavooripachadi.engage.OutboundChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record TemplateUpsertRequest(
        @NotBlank String code,
        @NotNull OutboundChannel channel,
        String locale,
        String subject,
        String bodyHtml,
        String bodyText,
        Boolean enabled
) {}