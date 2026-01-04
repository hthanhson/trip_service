package com.datn.trip_service.repository;

import com.datn.trip_service.model.Plan;
import com.datn.trip_service.model.PlanType;
import com.datn.trip_service.model.Trip;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;
import com.datn.trip_service.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class TripRepository {

    private static final String COLLECTION_NAME = "trips";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
    
    // Helper method to parse LocalDateTime from Firestore (handles both Timestamp and String)
    private LocalDateTime parseLocalDateTime(Object value) {
        if (value == null) return null;
        
        if (value instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) value;
            return LocalDateTime.ofInstant(timestamp.toDate().toInstant(), ZoneId.systemDefault());
        } else if (value instanceof String) {
            try {
                return LocalDateTime.parse((String) value);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
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
        map.put("isPublic", trip.getIsPublic() != null ? trip.getIsPublic() : "none");
        map.put("coverPhoto", trip.getCoverPhoto());
        map.put("content", trip.getContent());
        map.put("tags", trip.getTags());
        map.put("createdAt", trip.getCreatedAt() != null ? trip.getCreatedAt().toString() : null);
        map.put("sharedAt", trip.getSharedAt() != null ? trip.getSharedAt().toString() : null);
        
        // Add members list
        if (trip.getMembers() != null && !trip.getMembers().isEmpty()) {
            List<Map<String, Object>> membersList = new ArrayList<>();
            for (var member : trip.getMembers()) {
                Map<String, Object> memberMap = new HashMap<>();
                memberMap.put("id", member.getId());
                memberMap.put("firstName", member.getFirstName());
                memberMap.put("lastName", member.getLastName());
                memberMap.put("email", member.getEmail());
                memberMap.put("profilePicture", member.getProfilePicture());
                memberMap.put("role", member.getRole());
                memberMap.put("enabled", member.getEnabled());
                membersList.add(memberMap);
            }
            map.put("members", membersList);
        } else {
            map.put("members", new ArrayList<>());
        }
        
        // Add sharedWithUsers list
        if (trip.getSharedWithUsers() != null) {
            List<Map<String, Object>> sharedUsersList = trip.getSharedWithUsers().stream()
                    .map(this::convertUserToMap)
                    .collect(Collectors.toList());
            map.put("sharedWithUsers", sharedUsersList);
        } else {
            map.put("sharedWithUsers", new ArrayList<>());
        }
        
        return map;
    }
    
    // Helper method to convert User to Map
    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("email", user.getEmail());
        userMap.put("profilePicture", user.getProfilePicture());
        userMap.put("role", user.getRole());
        userMap.put("enabled", user.getEnabled());
        return userMap;
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
        
        trip.setIsPublic(document.getString("isPublic"));
        trip.setCoverPhoto(document.getString("coverPhoto"));
        trip.setContent(document.getString("content"));
        trip.setTags(document.getString("tags"));
        
        // Parse datetime fields using helper (handles both Timestamp and String)
        trip.setCreatedAt(parseLocalDateTime(document.get("createdAt")));
        trip.setSharedAt(parseLocalDateTime(document.get("sharedAt")));
        
        // Load members list
        try {
            List<Map<String, Object>> membersList = (List<Map<String, Object>>) document.get("members");
            if (membersList != null && !membersList.isEmpty()) {
                List<User> members = new ArrayList<>();
                for (Map<String, Object> memberMap : membersList) {
                    User member = User.builder()
                            .id((String) memberMap.get("id"))
                            .firstName((String) memberMap.get("firstName"))
                            .lastName((String) memberMap.get("lastName"))
                            .email((String) memberMap.get("email"))
                            .profilePicture((String) memberMap.get("profilePicture"))
                            .role((String) memberMap.get("role"))
                            .enabled((Boolean) memberMap.get("enabled"))
                            .build();
                    members.add(member);
                }
                trip.setMembers(members);
            } else {
                trip.setMembers(new ArrayList<>());
            }
        } catch (Exception e) {
            System.err.println("Failed to parse members: " + e.getMessage());
            trip.setMembers(new ArrayList<>());
        }
        
        // Load sharedWithUsers list
        try {
            List<Map<String, Object>> sharedUsersList = (List<Map<String, Object>>) document.get("sharedWithUsers");
            if (sharedUsersList != null && !sharedUsersList.isEmpty()) {
                List<User> sharedUsers = new ArrayList<>();
                for (Map<String, Object> userMap : sharedUsersList) {
                    User user = User.builder()
                            .id((String) userMap.get("id"))
                            .firstName((String) userMap.get("firstName"))
                            .lastName((String) userMap.get("lastName"))
                            .email((String) userMap.get("email"))
                            .profilePicture((String) userMap.get("profilePicture"))
                            .role((String) userMap.get("role"))
                            .enabled((Boolean) userMap.get("enabled"))
                            .build();
                    sharedUsers.add(user);
                }
                trip.setSharedWithUsers(sharedUsers);
            } else {
                trip.setSharedWithUsers(new ArrayList<>());
            }
        } catch (Exception e) {
            System.err.println("Failed to parse sharedWithUsers: " + e.getMessage());
            trip.setSharedWithUsers(new ArrayList<>());
        }
        
        // Load plans from separate collection
        try {
            Firestore firestore = getFirestore();
            QuerySnapshot plansSnapshot = firestore.collection("plans")
                    .whereEqualTo("tripId", document.getId())
                    .get()
                    .get();
            
            List<Plan> plans = new ArrayList<>();
            System.out.println("Loading plans for trip: " + document.getId() + ", found " + plansSnapshot.size() + " plans");
            
            for (DocumentSnapshot planDoc : plansSnapshot.getDocuments()) {
                System.out.println("Plan doc ID: " + planDoc.getId() + ", data: " + planDoc.getData());
                Plan plan = convertDocumentToPlan(planDoc);
                if (plan != null) {
                    plans.add(plan);
                }
            }
            trip.setPlans(plans);
        } catch (Exception e) {
            // If loading plans fails, set empty list
            System.err.println("Failed to load plans for trip " + document.getId() + ": " + e.getMessage());
            e.printStackTrace();
            trip.setPlans(new ArrayList<>());
        }
        
        return trip;
    }
    
    private Plan convertDocumentToPlan(DocumentSnapshot document) {
        Plan plan = new Plan();
        plan.setId(document.getId());
        plan.setTitle(document.getString("title"));
        plan.setAddress(document.getString("address"));
        plan.setLocation(document.getString("location"));
        
        // Parse startTime using helper
        plan.setStartTime(parseLocalDateTime(document.get("startTime")));
        
        plan.setExpense(document.getDouble("expense"));
        plan.setPhotoUrl(document.getString("photoUrl"));
        
        // Load photos list
        Object photosObj = document.get("photos");
        if (photosObj instanceof List) {
            plan.setPhotos((List<String>) photosObj);
        } else {
            plan.setPhotos(new ArrayList<>());
        }
        
        // Parse type
        String typeStr = document.getString("type");
        if (typeStr != null) {
            try {
                plan.setType(PlanType.valueOf(typeStr));
            } catch (Exception e) {
                // If parsing fails, set to null
            }
        }
        
        // Parse createdAt using helper
        plan.setCreatedAt(parseLocalDateTime(document.get("createdAt")));
        
        return plan;
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

    /**
     * Find trips where user is a member (not the creator)
     */
    public List<Trip> findTripsByMemberId(String userId) {
        try {
            Firestore firestore = getFirestore();
            // Get all trips and filter in memory to check members array
            QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                    .get()
                    .get();
            
            List<Trip> trips = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                // Skip if user is the creator
                String creatorId = document.getString("userId");
                if (userId.equals(creatorId)) {
                    continue;
                }
                
                // Check if user is in members list
                List<Map<String, Object>> membersList = (List<Map<String, Object>>) document.get("members");
                if (membersList != null) {
                    boolean isMember = membersList.stream()
                            .anyMatch(member -> userId.equals(member.get("id")));
                    
                    if (isMember) {
                        Trip trip = convertDocumentToTrip(document);
                        if (trip != null) {
                            trips.add(trip);
                        }
                    }
                }
            }
            return trips;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find trips by member id", e);
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

    public List<Trip> findTripsStartingOnDate(LocalDate date) {
        try {
            Firestore firestore = getFirestore();
            
            // Convert LocalDate to String for Firestore query
            String dateString = date.toString();
            
            QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("startDate", dateString)
                    .get()
                    .get();
            
            List<Trip> trips = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Trip trip = convertDocumentToTrip(document);
                if (trip != null) {
                    trips.add(trip);
                }
            }
            
            System.out.println("Found " + trips.size() + " trip(s) starting on " + dateString);
            return trips;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find trips by start date", e);
        }
    }

    /**
     * Find all active trips (trips that are currently ongoing)
     * Active = startDate <= today <= endDate
     */
    public List<Trip> findActiveTrips(LocalDate today) {
        try {
            Firestore firestore = getFirestore();
            
            // Get all trips and filter in memory (Firestore doesn't support range queries on different fields easily)
            QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                    .get()
                    .get();
            
            List<Trip> activeTrips = new ArrayList<>();
            String todayString = today.toString();
            
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                String startDateStr = document.getString("startDate");
                String endDateStr = document.getString("endDate");
                
                if (startDateStr != null && endDateStr != null) {
                    // Check if today is between start and end date
                    if (startDateStr.compareTo(todayString) <= 0 && endDateStr.compareTo(todayString) >= 0) {
                        Trip trip = convertDocumentToTrip(document);
                        if (trip != null) {
                            activeTrips.add(trip);
                        }
                    }
                }
            }
            
            System.out.println("Found " + activeTrips.size() + " active trip(s) on " + todayString);
            return activeTrips;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find active trips", e);
        }
    }

    /**
     * Find upcoming trips (trips that will start in the future)
     * Upcoming = startDate > today
     * Sorted by startDate ascending (nearest first)
     */
    public List<Trip> findUpcomingTrips(LocalDate today) {
        try {
            Firestore firestore = getFirestore();
            
            String todayString = today.toString();
            
            // Query trips with startDate > today
            QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereGreaterThan("startDate", todayString)
                    .get()
                    .get();
            
            List<Trip> upcomingTrips = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Trip trip = convertDocumentToTrip(document);
                if (trip != null) {
                    upcomingTrips.add(trip);
                }
            }
            
            // Sort by startDate ascending (nearest first)
            upcomingTrips.sort((t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate()));
            
            System.out.println("Found " + upcomingTrips.size() + " upcoming trip(s) after " + todayString);
            return upcomingTrips;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find upcoming trips", e);
        }
    }

}
