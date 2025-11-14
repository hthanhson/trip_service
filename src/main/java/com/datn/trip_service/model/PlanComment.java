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
public class PlanComment {

    private Long id;
    private Long planId; // Reference to Plan
    private String userId; // Reference to User (Firebase UID)
    private Long parentId; // Support for nested/threaded comments
    private String content;
    private LocalDateTime createdAt;
}
