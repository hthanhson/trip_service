package com.datn.trip_service.model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanComment {

    private Long id;
    private String planId; // Reference to Plan
    private String userId; // Reference to User (Firebase UID)
    private String userName; // User's display name
    private String userAvatar; // User's avatar URL
    private String parentId; // Support for nested/threaded comments
    private String content;
    private Timestamp createdAt;
}
