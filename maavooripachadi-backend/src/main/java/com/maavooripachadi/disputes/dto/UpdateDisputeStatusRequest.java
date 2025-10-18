package com.maavooripachadi.disputes.dto;


import com.maavooripachadi.disputes.DisputeStatus;
import jakarta.validation.constraints.NotNull;


public record UpdateDisputeStatusRequest(@NotNull DisputeStatus status, String note) {}