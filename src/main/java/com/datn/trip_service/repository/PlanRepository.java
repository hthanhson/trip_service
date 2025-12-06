package com.datn.trip_service.repository;

import com.datn.trip_service.model.Plan;
import com.datn.trip_service.model.PlanType;
import com.datn.trip_service.model.plan.*;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Repository
public class PlanRepository {
    
    private static final String COLLECTION_NAME = "plans";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
    
    public Plan save(Plan plan) {
        try {
            Firestore firestore = getFirestore();
            DocumentReference docRef;
            
            if (plan.getId() == null || plan.getId().isEmpty()) {
                // Create new document with auto-generated ID
                docRef = firestore.collection(COLLECTION_NAME).document();
                plan.setId(docRef.getId());
            } else {
                // Update existing document
                docRef = firestore.collection(COLLECTION_NAME).document(plan.getId());
            }
            
            // Set created timestamp if new
            if (plan.getCreatedAt() == null) {
                plan.setCreatedAt(LocalDateTime.now());
            }
            
            // Convert Plan to Map
            Map<String, Object> planData = convertPlanToMap(plan);
            
            docRef.set(planData).get();
            return plan;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to save plan", e);
        }
    }
    
    public Optional<Plan> findById(String id) {
        try {
            Firestore firestore = getFirestore();
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            DocumentSnapshot document = docRef.get().get();
            
            if (document.exists()) {
                Plan plan = convertDocumentToPlan(document);
                return Optional.ofNullable(plan);
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find plan", e);
        }
    }
    
    public List<Plan> findByTripId(String tripId) {
        try {
            Firestore firestore = getFirestore();
            QuerySnapshot querySnapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("tripId", tripId)
                    .get()
                    .get();
            
            List<Plan> plans = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Plan plan = convertDocumentToPlan(document);
                if (plan != null) {
                    plans.add(plan);
                }
            }
            
            // Sort by startTime in Java instead of Firestore
            plans.sort((p1, p2) -> {
                if (p1.getStartTime() == null && p2.getStartTime() == null) return 0;
                if (p1.getStartTime() == null) return 1;
                if (p2.getStartTime() == null) return -1;
                return p1.getStartTime().compareTo(p2.getStartTime());
            });
            
            return plans;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to find plans by tripId", e);
        }
    }
    
    public void deleteById(String id) {
        try {
            Firestore firestore = getFirestore();
            firestore.collection(COLLECTION_NAME).document(id).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to delete plan", e);
        }
    }
    
    private Map<String, Object> convertPlanToMap(Plan plan) {
        Map<String, Object> map = new HashMap<>();
        map.put("tripId", plan.getTripId());
        map.put("title", plan.getTitle());
        map.put("address", plan.getAddress());
        map.put("location", plan.getLocation());
        
        // Convert LocalDateTime to String for Firebase
        if (plan.getStartTime() != null) {
            map.put("startTime", plan.getStartTime().format(formatter));
        }
        
        map.put("expense", plan.getExpense());
        map.put("photoUrl", plan.getPhotoUrl());
        
        // Add photos list - ensure it's not null
        if (plan.getPhotos() != null && !plan.getPhotos().isEmpty()) {
            map.put("photos", plan.getPhotos());
        } else {
            map.put("photos", new ArrayList<String>());
        }
        
        map.put("type", plan.getType() != null ? plan.getType().name() : null);
        
        if (plan.getCreatedAt() != null) {
            map.put("createdAt", plan.getCreatedAt().format(formatter));
        }
        
        // Add specific fields based on plan type
        if (plan instanceof FlightPlan) {
            FlightPlan fp = (FlightPlan) plan;
            map.put("arrivalLocation", fp.getArrivalLocation());
            map.put("arrivalAddress", fp.getArrivalAddress());
            if (fp.getArrivalDate() != null) {
                map.put("arrivalDate", fp.getArrivalDate().format(formatter));
            }
        } else if (plan instanceof RestaurantPlan) {
            RestaurantPlan rp = (RestaurantPlan) plan;
            if (rp.getReservationDate() != null) {
                map.put("reservationDate", rp.getReservationDate().format(formatter));
            }
            if (rp.getReservationTime() != null) {
                map.put("reservationTime", rp.getReservationTime().format(formatter));
            }
        } else if (plan instanceof LodgingPlan) {
            LodgingPlan lp = (LodgingPlan) plan;
            if (lp.getCheckInDate() != null) {
                map.put("checkInDate", lp.getCheckInDate().format(formatter));
            }
            if (lp.getCheckOutDate() != null) {
                map.put("checkOutDate", lp.getCheckOutDate().format(formatter));
            }
            map.put("phone", lp.getPhone());
        } else if (plan instanceof ActivityPlan) {
            ActivityPlan ap = (ActivityPlan) plan;
            if (ap.getEndTime() != null) {
                map.put("endTime", ap.getEndTime().format(formatter));
            }
        } else if (plan instanceof BoatPlan) {
            BoatPlan bp = (BoatPlan) plan;
            if (bp.getArrivalTime() != null) {
                map.put("arrivalTime", bp.getArrivalTime().format(formatter));
            }
            map.put("arrivalLocation", bp.getArrivalLocation());
            map.put("arrivalAddress", bp.getArrivalAddress());
        } else if (plan instanceof CarRentalPlan) {
            CarRentalPlan cp = (CarRentalPlan) plan;
            if (cp.getPickupDate() != null) {
                map.put("pickupDate", cp.getPickupDate().format(formatter));
            }
            if (cp.getPickupTime() != null) {
                map.put("pickupTime", cp.getPickupTime().format(formatter));
            }
            map.put("phone", cp.getPhone());
        }
        
        return map;
    }
    
    private Plan convertDocumentToPlan(DocumentSnapshot document) {
        String typeStr = document.getString("type");
        PlanType planType = typeStr != null ? PlanType.valueOf(typeStr) : null;
        
        // Create specific plan type based on type field
        Plan plan;
        if (planType != null) {
            switch (planType) {
                case FLIGHT:
                    FlightPlan fp = new FlightPlan();
                    fp.setArrivalLocation(document.getString("arrivalLocation"));
                    fp.setArrivalAddress(document.getString("arrivalAddress"));
                    String arrivalDateStr = document.getString("arrivalDate");
                    if (arrivalDateStr != null) {
                        fp.setArrivalDate(LocalDateTime.parse(arrivalDateStr, formatter));
                    }
                    plan = fp;
                    break;
                case RESTAURANT:
                    RestaurantPlan rp = new RestaurantPlan();
                    String reservationDateStr = document.getString("reservationDate");
                    if (reservationDateStr != null) {
                        rp.setReservationDate(LocalDateTime.parse(reservationDateStr, formatter));
                    }
                    String reservationTimeStr = document.getString("reservationTime");
                    if (reservationTimeStr != null) {
                        rp.setReservationTime(LocalDateTime.parse(reservationTimeStr, formatter));
                    }
                    plan = rp;
                    break;
                case LODGING:
                    LodgingPlan lp = new LodgingPlan();
                    String checkInDateStr = document.getString("checkInDate");
                    if (checkInDateStr != null) {
                        lp.setCheckInDate(LocalDateTime.parse(checkInDateStr, formatter));
                    }
                    String checkOutDateStr = document.getString("checkOutDate");
                    if (checkOutDateStr != null) {
                        lp.setCheckOutDate(LocalDateTime.parse(checkOutDateStr, formatter));
                    }
                    lp.setPhone(document.getString("phone"));
                    plan = lp;
                    break;
                case ACTIVITY:
                    ActivityPlan ap = new ActivityPlan();
                    String endTimeStr = document.getString("endTime");
                    if (endTimeStr != null) {
                        ap.setEndTime(LocalDateTime.parse(endTimeStr, formatter));
                    }
                    plan = ap;
                    break;
                case BOAT:
                    BoatPlan bp = new BoatPlan();
                    String arrivalTimeStr = document.getString("arrivalTime");
                    if (arrivalTimeStr != null) {
                        bp.setArrivalTime(LocalDateTime.parse(arrivalTimeStr, formatter));
                    }
                    bp.setArrivalLocation(document.getString("arrivalLocation"));
                    bp.setArrivalAddress(document.getString("arrivalAddress"));
                    plan = bp;
                    break;
                case CAR_RENTAL:
                    CarRentalPlan cp = new CarRentalPlan();
                    String pickupDateStr = document.getString("pickupDate");
                    if (pickupDateStr != null) {
                        cp.setPickupDate(LocalDateTime.parse(pickupDateStr, formatter));
                    }
                    String pickupTimeStr = document.getString("pickupTime");
                    if (pickupTimeStr != null) {
                        cp.setPickupTime(LocalDateTime.parse(pickupTimeStr, formatter));
                    }
                    cp.setPhone(document.getString("phone"));
                    plan = cp;
                    break;
                default:
                    plan = new Plan();
            }
        } else {
            plan = new Plan();
        }
        
        // Set common fields
        plan.setId(document.getId());
        plan.setTripId(document.getString("tripId"));
        plan.setTitle(document.getString("title"));
        plan.setAddress(document.getString("address"));
        plan.setLocation(document.getString("location"));
        
        // Parse LocalDateTime from String
        String startTimeStr = document.getString("startTime");
        if (startTimeStr != null) {
            plan.setStartTime(LocalDateTime.parse(startTimeStr, formatter));
        }
        
        plan.setExpense(document.getDouble("expense"));
        plan.setPhotoUrl(document.getString("photoUrl"));
        
        // Parse photos list
        Object photosObj = document.get("photos");
        if (photosObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> photos = (List<String>) photosObj;
            plan.setPhotos(photos);
        } else {
            plan.setPhotos(new ArrayList<>());
        }
        
        plan.setType(planType);
        
        String createdAtStr = document.getString("createdAt");
        if (createdAtStr != null) {
            plan.setCreatedAt(LocalDateTime.parse(createdAtStr, formatter));
        }
        
        return plan;
    }
}
