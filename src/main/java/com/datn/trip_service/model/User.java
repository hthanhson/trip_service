package com.datn.trip_service.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String id; // Firebase UID
    private String firstName;
    private String lastName;
    private String email;
    private String password; // Nullable for OAuth users
    
    @Builder.Default
    private String role = "user"; // guest | user | admin
    
    private String profilePicture;
    private AuthProvider provider;
    private String providerId; // Google ID, Facebook ID, etc.
    
    @Builder.Default
    private Boolean enabled = true;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
