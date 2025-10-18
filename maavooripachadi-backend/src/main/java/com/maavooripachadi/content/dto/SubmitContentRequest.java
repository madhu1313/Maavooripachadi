package com.maavooripachadi.content.dto;


import jakarta.validation.constraints.NotBlank;


public record SubmitContentRequest(
        @NotBlank String type, // RECIPE or BLOG
        @NotBlank String id
) {}