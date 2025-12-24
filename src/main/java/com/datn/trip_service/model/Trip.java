package com.datn.trip_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    private String id;
    private String userId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @Builder.Default
    private String isPublic = "none"; // Values: "none", "public", "follower"
    
    private String coverPhoto;
    private String content; // User's feelings/review about their trip
    private String tags; // JSON array: ["food", "beach", "adventure"] for categorization
    private List<Plan> plans;
    private List<User> members; // List of members who can participate and manage plans in the trip
    private List<User> sharedWithUsers; // When isPublic="follower", share only with these users; empty = all followers
    private LocalDateTime createdAt;
    private LocalDateTime sharedAt; // Timestamp when trip was first shared
}
