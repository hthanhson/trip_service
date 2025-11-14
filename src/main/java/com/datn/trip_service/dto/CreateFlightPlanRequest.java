package com.datn.trip_service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateFlightPlanRequest extends CreatePlanRequest {
    private String arrivalLocation;
    private String arrivalAddress;
    private String arrivalDate;  // ISO format: yyyy-MM-dd'T'HH:mm:ss
}
