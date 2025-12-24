package com.datn.trip_service.dto;

import com.datn.trip_service.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTripRequest {
    private String userId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String isPublic = "none"; // Values: "none", "public", "follower"
    private String coverPhoto;
    private String content;
    private String tags;
    private List<User> members; // List of members who can participate and manage plans
    private List<User> sharedWithUsers; // Specific users to share with when isPublic="follower"
    private LocalDateTime sharedAt;
}
