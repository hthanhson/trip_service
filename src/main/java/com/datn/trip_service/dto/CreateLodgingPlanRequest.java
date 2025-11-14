package com.datn.trip_service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateLodgingPlanRequest extends CreatePlanRequest {
    private String checkInDate;   // ISO format: yyyy-MM-dd'T'HH:mm:ss
    private String checkOutDate;  // ISO format: yyyy-MM-dd'T'HH:mm:ss
    private String phone;
}
