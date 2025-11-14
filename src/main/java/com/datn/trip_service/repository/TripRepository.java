package com.datn.trip_service.repository;

import com.datn.trip_service.model.Trip;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class TripRepository {

    private static final String COLLECTION_NAME = "trips";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public Trip save(Trip trip) {
        try {
            Firestore firestore = getFirestore();
            
            // Generate ID if not exists
            if (trip.getId() == null || trip.getId().isEmpty()) {
                // Use Firestore auto-generated ID
                DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
                trip.setId(docRef.getId());
            }
            
            // Set createdAt if not exists
            if (trip.getCreatedAt() == null) {
                trip.setCreatedAt(LocalDateTime.now());
            }
            
            // Convert Trip to Map for Firebase
            Map<String, Object> tripData = convertTripToMap(trip);
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME)
                    .document(trip.getId());
            docRef.set(tripData).get();
            
            return trip;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save trip", e);
        }
    }
    
    private Map<String, Object> convertTripToMap(Trip trip) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", trip.getId());
        map.put("userId", trip.getUserId());
        map.put("title", trip.getTitle());
        map.put("startDate", trip.getStartDate() != null ? trip.getStartDate().toString() : null);
        map.put("endDate", trip.getEndDate() != null ? trip.getEndDate().toString() : null);
        map.put("isPublic", trip.getIsPublic() != null ? trip.getIsPublic() : false);
        map.put("coverPhoto", trip.getCoverPhoto());
        map.put("content", trip.getContent());
        map.put("tags", trip.getTags());
        map.put("createdAt", trip.getCreatedAt() != null ? trip.getCreatedAt().toString() : null);
        return map;
    }

    public Optional<Trip> findById(String id) {
        try {
            Firestore firestore = getFirestore();
            DocumentReference docRef = firestore.collection(COLLECTION_NAME)
                    .document(id);
            DocumentSnapshot document = docRef.get().get();
            
            if (document.exists()) {
                Trip trip = convertDocumentToTrip(document);
                return Optional.ofNullable(trip);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find trip", e);
        }
    }
    
    private Trip convertDocumentToTrip(DocumentSnapshot document) {
        Trip trip = new Trip();
        trip.setId(document.getId());
        trip.setUserId(document.getString("userId"));
        trip.setTitle(document.getString("title"));
        
        String startDateStr = document.getString("startDate");
        if (startDateStr != null) {
            trip.setStartDate(LocalDate.parse(startDateStr));
        }
        
        String endDateStr = document.getString("endDate");
        if (endDateStr != null) {
            trip.setEndDate(LocalDate.parse(endDateStr));
        }
        
        trip.setIsPublic(document.getBoolean("isPublic"));
        trip.setCoverPhoto(document.getString("coverPhoto"));
        trip.setContent(document.getString("content"));
        trip.setTags(document.getString("tags"));
        
        String createdAtStr = document.getString("createdAt");
        if (createdAtStr != null) {
            trip.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }
        
        return trip;
    }

    public List<Trip> findByUserId(String userId) {
        try {
            Firestore firestore = getFirestore();
            QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .get()
                    .get();
            
            List<Trip> trips = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Trip trip = convertDocumentToTrip(document);
                if (trip != null) {
                    trips.add(trip);
                }
            }
            return trips;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find trips by userId", e);
        }
    }

    public void delete(Trip trip) {
        deleteById(trip.getId());
    }

    public void deleteById(String id) {
        try {
            Firestore firestore = getFirestore();
            firestore.collection(COLLECTION_NAME)
                    .document(id)
                    .delete()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete trip", e);
        }
    }
}
