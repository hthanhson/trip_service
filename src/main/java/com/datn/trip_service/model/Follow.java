package com.datn.trip_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    private Long id;
    private String followerId; // Firebase UID
    private String followingId; // Firebase UID
    private LocalDateTime createdAt;
}
