package com.maavooripachadi.engage.dto;


import jakarta.validation.constraints.NotBlank;
import java.util.Map;


public record PreviewRequest(@NotBlank String templateCode, Map<String,Object> variables) {}