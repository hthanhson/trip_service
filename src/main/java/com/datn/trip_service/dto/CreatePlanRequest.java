package com.datn.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanRequest {
    private String tripId;
    private String title;
    private String address;
    private String location;
    private String startTime;        // ISO format: yyyy-MM-dd'T'HH:mm:ss
    private String endTime;          // ISO format: yyyy-MM-dd'T'HH:mm:ss
    private Double expense;
    private String photoUrl;
    private String type;             // PlanType enum name
}
