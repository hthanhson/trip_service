package com.datn.trip_service.service;

import com.datn.trip_service.dto.CreateTripRequest;
import com.datn.trip_service.model.Trip;
import com.datn.trip_service.model.User;
import com.datn.trip_service.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private FileStorageService fileStorageService;

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
}
