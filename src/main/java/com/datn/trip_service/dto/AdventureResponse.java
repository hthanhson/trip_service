package com.datn.trip_service.dto;

import com.datn.trip_service.model.Plan;
import com.datn.trip_service.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for Adventure section - returns all necessary data in 1 API call
 * Replaces 11 separate API calls with a single optimized endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdventureResponse {
    private List<AdventureItem> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdventureItem {
        // Trip ID
        private String tripId;
        
        // Trip details (embedded)
        private TripDetail trip;
        
        // User details (embedded)
        private UserDetail user;
        
        // Calculated fields for UI
        private int duration; // Number of days
        private String startDateText; // e.g., "January 2026"
        private String durationText; // e.g., "5 days"
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripDetail {
        private String id;
        private String userId;
        private String title;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
        
        private String isPublic;
        private String coverPhoto;
        private String content;
        private String tags;
        private List<Plan> plans;
        private List<User> members;
        private List<User> sharedWithUsers;
        private LocalDateTime createdAt;
        private LocalDateTime sharedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDetail {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String profilePicture;
        private String role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
