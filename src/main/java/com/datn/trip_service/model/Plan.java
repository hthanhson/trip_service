package com.datn.trip_service.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    private String id;
    private String tripId; // Reference to Trip
    private String title;
    private String address;
    private String location;
    private LocalDateTime startTime;
    private Double expense;
    private String photoUrl;
    private PlanType type;
    private List<PlanLike> likes;
    private List<PlanComment> comments;
    private LocalDateTime createdAt;
}
