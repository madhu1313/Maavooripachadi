package com.maavooripachadi.engage.dto;


import jakarta.validation.constraints.NotBlank;


public record RegisterPushTokenRequest(
        @NotBlank String deviceId,
        @NotBlank String token,
        String platform,
        String userId
) {}
