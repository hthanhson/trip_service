package com.datn.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime sharedAt;
}
