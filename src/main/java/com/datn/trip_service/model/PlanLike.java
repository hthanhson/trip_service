package com.datn.trip_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanLike {

    private Long id;
    private Long planId; // Reference to Plan
    private String userId; // Reference to User (Firebase UID)
    private LocalDateTime createdAt;
}
