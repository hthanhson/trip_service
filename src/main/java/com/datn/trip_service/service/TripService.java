package com.datn.trip_service.service;

import com.datn.trip_service.dto.AdventureResponse;
import com.datn.trip_service.dto.CreateTripRequest;
import com.datn.trip_service.model.Plan;
import com.datn.trip_service.model.Trip;
import com.datn.trip_service.model.User;
import com.datn.trip_service.repository.PlanRepository;
import com.datn.trip_service.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private PlanRepository planRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private UserService userService;

    public Trip createTrip(CreateTripRequest request) {
        Trip trip = new Trip();
        trip.setUserId(request.getUserId());
        trip.setTitle(request.getTitle());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : null);
        trip.setCoverPhoto(request.getCoverPhoto());
        trip.setContent(request.getContent());
        trip.setTags(request.getTags());
        trip.setMembers(request.getMembers() != null ? request.getMembers() : new ArrayList<>());
        trip.setSharedWithUsers(request.getSharedWithUsers() != null ? request.getSharedWithUsers() : new ArrayList<>());
        
        return tripRepository.save(trip);
    }

    public Trip getTripById(String id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
    }
    
    /**
     * Get trip with full plan details - optimized for UI
     * Returns trip WITH all complete plan information embedded
     * This prevents the need to make separate API calls for each plan
     */
    public Trip getTripWithFullPlans(String id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
        
        // Load all plans for this trip with full details
        List<Plan> plans = planRepository.findByTripId(id);
        
        // Populate user info for comments in each plan
        if (plans != null && !plans.isEmpty()) {
            for (Plan plan : plans) {
                if (plan.getComments() != null && !plan.getComments().isEmpty()) {
                    populateCommentUserInfo(plan.getComments());
                }
            }
            trip.setPlans(plans);
        } else {
            trip.setPlans(new ArrayList<>());
        }
        
        return trip;
    }
    
    /**
     * Populate userName and userAvatar for comments
     */
    private void populateCommentUserInfo(List<com.datn.trip_service.model.PlanComment> comments) {
        for (com.datn.trip_service.model.PlanComment comment : comments) {
            if (comment.getUserId() != null) {
                try {
                    User user = userService.getUserById(comment.getUserId());
                    if (user != null) {
                        comment.setUserName(user.getFirstName() + " " + user.getLastName());
                        comment.setUserAvatar(user.getProfilePicture());
                    }
                } catch (Exception e) {
                    // Log but don't fail - just leave userName/userAvatar as null
                    System.err.println("Failed to get user info for comment userId: " + comment.getUserId());
                }
            }
        }
    }

    public List<Trip> getTripsByUserId(String userId) {
        return tripRepository.findByUserId(userId);
    }

    public List<Trip> getTripsByMemberId(String userId) {
        return tripRepository.findTripsByMemberId(userId);
    }

    public Trip updateTrip(String id, CreateTripRequest request) {
        Trip trip = getTripById(id);
        
        // Check if coverPhoto is being changed and delete old photo if exists
        if (request.getCoverPhoto() != null && 
            !request.getCoverPhoto().equals(trip.getCoverPhoto()) &&
            trip.getCoverPhoto() != null && 
            !trip.getCoverPhoto().isEmpty()) {
            try {
                // Delete old cover photo from storage
                fileStorageService.deleteFile(trip.getCoverPhoto());
            } catch (Exception e) {
                // Log error but continue with update
                System.err.println("Failed to delete old cover photo: " + e.getMessage());
            }
        }
        
        trip.setTitle(request.getTitle());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setIsPublic(request.getIsPublic());
        trip.setCoverPhoto(request.getCoverPhoto());
        trip.setContent(request.getContent());
        trip.setTags(request.getTags());
        trip.setMembers(request.getMembers());
        // Update members and sharing settings
//        if (request.getMembers() != null) {
//            trip.setMembers(request.getMembers());
//        }
        if (request.getSharedWithUsers() != null) {
            trip.setSharedWithUsers(request.getSharedWithUsers());
        }
        
        // Only set sharedAt if it's the first time sharing (trip doesn't have sharedAt yet)
        if (trip.getSharedAt() == null && request.getSharedAt() != null) {
            trip.setSharedAt(request.getSharedAt());
        }
        // Keep existing sharedAt if already set, don't update it
        
        return tripRepository.save(trip);
    }

    public void deleteTrip(String id) {
        tripRepository.deleteById(id);
    }
    
    // Add member to trip
    public Trip addMember(String tripId, User member) {
        Trip trip = getTripById(tripId);
        
        if (trip.getMembers() == null) {
            trip.setMembers(new ArrayList<>());
        }
        
        // Check if member already exists
        boolean memberExists = trip.getMembers().stream()
                .anyMatch(m -> m.getId().equals(member.getId()));
        
        if (!memberExists) {
            trip.getMembers().add(member);
            return tripRepository.save(trip);
        }
        
        return trip;
    }
    
    // Remove member from trip
    public Trip removeMember(String tripId, String userId) {
        Trip trip = getTripById(tripId);
        
        if (trip.getMembers() != null) {
            trip.getMembers().removeIf(member -> member.getId().equals(userId));
            return tripRepository.save(trip);
        }
        
        return trip;
    }
    
    // Update shared users list
    public Trip updateSharedUsers(String tripId, List<String> userIds) {
        Trip trip = getTripById(tripId);
        // For backward compatibility, convert IDs to User objects if needed
        // In practice, this method might be deprecated in favor of full user objects
        trip.setSharedWithUsers(new ArrayList<>()); // Clear for now, or convert IDs to User objects
        return tripRepository.save(trip);
    }
    
    // Check if user is member of trip
    public boolean isMember(String tripId, String userId) {
        Trip trip = getTripById(tripId);
        
        // Creator is always a member
        if (trip.getUserId().equals(userId)) {
            return true;
        }
        
        // Check if user is in members list
        if (trip.getMembers() != null) {
            return trip.getMembers().stream()
                    .anyMatch(member -> member.getId().equals(userId));
        }
        
        return false;
    }
    
    // Check if user can view the trip based on sharing settings
    public boolean canViewTrip(String tripId, String userId, List<String> userFollowerIds) {
        Trip trip = getTripById(tripId);
        
        // Creator can always view
        if (trip.getUserId().equals(userId)) {
            return true;
        }
        
        // Members can always view
        if (isMember(tripId, userId)) {
            return true;
        }
        
        // Public trips can be viewed by anyone
        if ("public".equals(trip.getIsPublic())) {
            return true;
        }
        
        // Follower-only trips
        if ("follower".equals(trip.getIsPublic())) {
            // If sharedWithUsers is specified and not empty, check if user is in the list
            if (trip.getSharedWithUsers() != null && !trip.getSharedWithUsers().isEmpty()) {
                return trip.getSharedWithUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId));
            }
            // If sharedWithUsers is empty, share with all followers
            else if (userFollowerIds != null) {
                return userFollowerIds.contains(trip.getUserId());
            }
        }
        
        return false;
    }
    
    /**
     * Get Adventure trips - optimized endpoint that returns all data in 1 call
     * Replaces 11 separate API calls (1 discover + 10 trip/user details)
     */
    public AdventureResponse getAdventureTrips(String userId, int limit) {
        try {
            // 1. Get public trips (excluding user's own trips)
            List<Trip> publicTrips = tripRepository.findPublicTripsForAdventure(userId, limit);
            
            // 2. Build adventure items with all details embedded
            List<AdventureResponse.AdventureItem> items = publicTrips.stream()
                .map(trip -> {
                    try {
                        // Get user details (trip owner)
                        User tripOwner = getUserById(trip.getUserId());
                        
                        // Calculate duration
                        int duration = (int) ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate()) + 1;
                        
                        // Format start date text (e.g., "January 2026")
                        String monthName = trip.getStartDate().getMonth()
                            .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                        int year = trip.getStartDate().getYear();
                        String startDateText = monthName + " " + year;
                        
                        // Format duration text (e.g., "5 days")
                        String durationText = duration + " days";
                        
                        // Build TripDetail
                        AdventureResponse.TripDetail tripDetail = AdventureResponse.TripDetail.builder()
                            .id(trip.getId())
                            .userId(trip.getUserId())
                            .title(trip.getTitle())
                            .startDate(trip.getStartDate())
                            .endDate(trip.getEndDate())
                            .isPublic(trip.getIsPublic())
                            .coverPhoto(trip.getCoverPhoto())
                            .content(trip.getContent())
                            .tags(trip.getTags())
                            .plans(trip.getPlans())
                            .members(trip.getMembers())
                            .sharedWithUsers(trip.getSharedWithUsers())
                            .createdAt(trip.getCreatedAt())
                            .sharedAt(trip.getSharedAt())
                            .build();
                        
                        // Build UserDetail
                        AdventureResponse.UserDetail userDetail = AdventureResponse.UserDetail.builder()
                            .id(tripOwner.getId())
                            .firstName(tripOwner.getFirstName())
                            .lastName(tripOwner.getLastName())
                            .email(tripOwner.getEmail())
                            .profilePicture(tripOwner.getProfilePicture())
                            .role(tripOwner.getRole())
                            .createdAt(tripOwner.getCreatedAt())
                            .updatedAt(tripOwner.getUpdatedAt())
                            .build();
                        
                        // Build AdventureItem
                        return AdventureResponse.AdventureItem.builder()
                            .tripId(trip.getId())
                            .trip(tripDetail)
                            .user(userDetail)
                            .duration(duration)
                            .startDateText(startDateText)
                            .durationText(durationText)
                            .build();
                            
                    } catch (Exception e) {
                        System.err.println("Error processing trip " + trip.getId() + ": " + e.getMessage());
                        return null;
                    }
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
            
            return AdventureResponse.builder()
                .items(items)
                .build();
                
        } catch (Exception e) {
            System.err.println("Error getting adventure trips: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get adventure trips", e);
        }
    }
    
    /**
     * Helper method to get user by ID
     */
    private User getUserById(String userId) {
        try {
            return userService.getUserById(userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user: " + userId, e);
        }
    }
}
