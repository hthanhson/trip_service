package com.datn.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTripRequest {
    private String userId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublic = false;
    private String coverPhoto;
    private String content;
    private String tags;
}
