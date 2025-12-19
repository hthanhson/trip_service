package com.datn.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String token; // FCM device token
    private String title;
    private String body;
    private String image; // Optional image URL
    private Map<String, String> data; // Additional data payload
}
