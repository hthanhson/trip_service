package com.datn.trip_service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateActivityPlanRequest extends CreatePlanRequest {
    // Activity uses the base endTime field, no additional fields needed
}
