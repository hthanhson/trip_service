package com.datn.trip_service.dto;

import com.datn.trip_service.model.Trip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private boolean success;
    private String message;
    private Trip data;
}
