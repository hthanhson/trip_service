package com.datn.trip_service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateBoatPlanRequest extends CreatePlanRequest {
    private String arrivalTime;      // ISO format: yyyy-MM-dd'T'HH:mm:ss
    private String arrivalLocation;
    private String arrivalAddress;
}
