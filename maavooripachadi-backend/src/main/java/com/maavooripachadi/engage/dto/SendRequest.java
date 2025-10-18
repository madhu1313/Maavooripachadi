package com.maavooripachadi.engage.dto;


import com.maavooripachadi.engage.OutboundChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;


public record SendRequest(
        @NotNull OutboundChannel channel,
        @NotBlank String templateCode,
        @NotBlank String target,
        Map<String,Object> variables
) {}