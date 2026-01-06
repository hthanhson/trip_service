package com.datn.trip_service.repository;

import com.datn.trip_service.model.Plan;
import com.datn.trip_service.model.PlanType;
import com.datn.trip_service.model.PlanLike;
import com.datn.trip_service.model.PlanComment;
import com.datn.trip_service.model.plan.ActivityPlan;
import com.datn.trip_service.model.plan.BoatPlan;
import com.datn.trip_service.model.plan.CarRentalPlan;
import com.datn.trip_service.model.plan.FlightPlan;
import com.datn.trip_service.model.plan.LodgingPlan;
import com.datn.trip_service.model.plan.RestaurantPlan;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private LocalDateTime parseLocalDateTime(Object value) {
        if (value == null) return null;
        
        if (value instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) value;
            return LocalDateTime.ofInstant(timestamp.toDate().toInstant(), ZoneId.systemDefault());
        } else if (value instanceof String) {
            try {
                return LocalDateTime.parse((String) value, formatter);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
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
                docRef = firestore.collection(COLLECTION_NAME).document(plan.getId());
            }

            if (plan.getCreatedAt() == null) {
                plan.setCreatedAt(LocalDateTime.now());
            }
            
            // Convert Plan to Map
            Map<String, Object> planData = convertPlanToMap(plan);

            docRef.set(planData, SetOptions.merge()).get();
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

        if (plan.getLikes() != null && !plan.getLikes().isEmpty()) {
            List<Map<String, Object>> likesList = new ArrayList<>();
            for (PlanLike like : plan.getLikes()) {
                Map<String, Object> likeMap = new HashMap<>();
                if (like.getId() != null) {
                    likeMap.put("id", like.getId());
                }
                if (like.getPlanId() != null) {
                    likeMap.put("planId", like.getPlanId());
                }
                if (like.getUserId() != null) {
                    likeMap.put("userId", like.getUserId());
                }
                if (like.getCreatedAt() != null) {
                    likeMap.put("createdAt", like.getCreatedAt());
                }
                likesList.add(likeMap);
            }
            map.put("likes", likesList);
        }
        
        if (plan.getComments() != null && !plan.getComments().isEmpty()) {
            List<Map<String, Object>> commentsList = new ArrayList<>();
            for (PlanComment comment : plan.getComments()) {
                Map<String, Object> commentMap = new HashMap<>();
                if (comment.getId() != null) {
                    commentMap.put("id", comment.getId());
                }
                if (comment.getPlanId() != null) {
                    commentMap.put("planId", comment.getPlanId());
                }
                if (comment.getUserId() != null) {
                    commentMap.put("userId", comment.getUserId());
                }

                if (comment.getParentId() != null) {
                    commentMap.put("parentId", comment.getParentId());
                }
                if (comment.getContent() != null) {
                    commentMap.put("content", comment.getContent());
                }
                if (comment.getCreatedAt() != null) {
                    commentMap.put("createdAt", comment.getCreatedAt());
                }
                commentsList.add(commentMap);
            }
            map.put("comments", commentsList);
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
                    fp.setArrivalDate(parseLocalDateTime(document.get("arrivalDate")));
                    plan = fp;
                    break;
                case RESTAURANT:
                    RestaurantPlan rp = new RestaurantPlan();
                    rp.setReservationDate(parseLocalDateTime(document.get("reservationDate")));
                    rp.setReservationTime(parseLocalDateTime(document.get("reservationTime")));
                    plan = rp;
                    break;
                case LODGING:
                    LodgingPlan lp = new LodgingPlan();
                    lp.setCheckInDate(parseLocalDateTime(document.get("checkInDate")));
                    lp.setCheckOutDate(parseLocalDateTime(document.get("checkOutDate")));
                    lp.setPhone(document.getString("phone"));
                    plan = lp;
                    break;
                case ACTIVITY:
                case TOUR:
                case THEATER:
                case SHOPPING:
                case CAMPING:
                case RELIGION:
                    ActivityPlan ap = new ActivityPlan();
                    ap.setEndTime(parseLocalDateTime(document.get("endTime")));
                    plan = ap;
                    break;
                case BOAT:
                    BoatPlan bp = new BoatPlan();
                    bp.setArrivalTime(parseLocalDateTime(document.get("arrivalTime")));
                    bp.setArrivalLocation(document.getString("arrivalLocation"));
                    bp.setArrivalAddress(document.getString("arrivalAddress"));
                    plan = bp;
                    break;
                case CAR_RENTAL:
                case TRAIN:
                    CarRentalPlan cp = new CarRentalPlan();
                    cp.setPickupDate(parseLocalDateTime(document.get("pickupDate")));
                    cp.setPickupTime(parseLocalDateTime(document.get("pickupTime")));
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
        
        // Parse LocalDateTime from Firestore (handles both Timestamp and String)
        plan.setStartTime(parseLocalDateTime(document.get("startTime")));
        
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
        
        plan.setCreatedAt(parseLocalDateTime(document.get("createdAt")));
        
        // Load likes and comments from Firestore to preserve them
        Object likesObj = document.get("likes");
        if (likesObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> likesList = (List<Map<String, Object>>) likesObj;
            List<PlanLike> likes = new ArrayList<>();
            for (Map<String, Object> likeMap : likesList) {
                PlanLike like = new PlanLike();
                if (likeMap.get("id") != null) {
                    like.setId(((Number) likeMap.get("id")).longValue());
                }
                // planId is String
                like.setPlanId((String) likeMap.get("planId"));
                like.setUserId((String) likeMap.get("userId"));
                // createdAt is Timestamp
                Object createdAtObj = likeMap.get("createdAt");
                if (createdAtObj instanceof Timestamp) {
                    like.setCreatedAt((Timestamp) createdAtObj);
                } else if (createdAtObj instanceof Number) {
                    // Convert from milliseconds to Timestamp
                    long millis = ((Number) createdAtObj).longValue();
                    like.setCreatedAt(Timestamp.ofTimeMicroseconds(millis * 1000));
                }
                likes.add(like);
            }
            plan.setLikes(likes);
        }
        
        Object commentsObj = document.get("comments");
        if (commentsObj instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> commentsList = (List<Map<String, Object>>) commentsObj;
            List<PlanComment> comments = new ArrayList<>();
            for (Map<String, Object> commentMap : commentsList) {
                PlanComment comment = new PlanComment();
                if (commentMap.get("id") != null) {
                    comment.setId(((Number) commentMap.get("id")).longValue());
                }
                // planId, parentId are String
                comment.setPlanId((String) commentMap.get("planId"));
                comment.setUserId((String) commentMap.get("userId"));
                comment.setUserName((String) commentMap.get("userName"));
                comment.setUserAvatar((String) commentMap.get("userAvatar"));
                comment.setParentId((String) commentMap.get("parentId"));
                comment.setContent((String) commentMap.get("content"));
                // createdAt is Timestamp
                Object createdAtObj = commentMap.get("createdAt");
                if (createdAtObj instanceof Timestamp) {
                    comment.setCreatedAt((Timestamp) createdAtObj);
                } else if (createdAtObj instanceof Number) {
                    // Convert from milliseconds to Timestamp
                    long millis = ((Number) createdAtObj).longValue();
                    comment.setCreatedAt(Timestamp.ofTimeMicroseconds(millis * 1000));
                }
                comments.add(comment);
            }
            plan.setComments(comments);
        }
        
        return plan;
    }
}
