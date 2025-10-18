package com.maavooripachadi.disputes.dto;


import com.maavooripachadi.disputes.*;
import java.time.OffsetDateTime;


public record DisputeEventResponse(Long id, DisputeEventType type, String payload, OffsetDateTime createdAt){
    public static DisputeEventResponse from(DisputeEvent e){
        return new DisputeEventResponse(e.getId(), e.getType(), e.getPayload(), e.getCreatedAt());
    }
}