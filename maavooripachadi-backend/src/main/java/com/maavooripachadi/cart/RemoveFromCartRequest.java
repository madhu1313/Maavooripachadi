package com.maavooripachadi.cart;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record RemoveFromCartRequest(
        @NotBlank String sessionId,
        @NotNull Long variantId
) {}