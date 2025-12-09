package com.datn.trip_service.service;

import com.datn.trip_service.dto.CreateTripRequest;
import com.datn.trip_service.model.Trip;
import com.datn.trip_service.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        trip.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : false);
        trip.setCoverPhoto(request.getCoverPhoto());
        trip.setContent(request.getContent());
        trip.setTags(request.getTags());
        
        return tripRepository.save(trip);
    }

    public Trip getTripById(String id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
    }

    public List<Trip> getTripsByUserId(String userId) {
        return tripRepository.findByUserId(userId);
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
        
        return tripRepository.save(trip);
    }

    public void deleteTrip(String id) {
        tripRepository.deleteById(id);
    }
}
