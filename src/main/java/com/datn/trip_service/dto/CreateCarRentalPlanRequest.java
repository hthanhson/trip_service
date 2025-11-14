package com.datn.trip_service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateCarRentalPlanRequest extends CreatePlanRequest {
    private String pickupDate;   // ISO format: yyyy-MM-dd'T'HH:mm:ss
    private String pickupTime;   // ISO format: yyyy-MM-dd'T'HH:mm:ss
    private String phone;
}
