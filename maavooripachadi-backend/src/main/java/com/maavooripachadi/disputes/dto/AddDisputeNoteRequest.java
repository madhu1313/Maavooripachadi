package com.maavooripachadi.disputes.dto;


import jakarta.validation.constraints.NotBlank;


public record AddDisputeNoteRequest(@NotBlank String note) {}