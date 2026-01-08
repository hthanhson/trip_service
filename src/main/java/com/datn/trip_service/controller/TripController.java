package com.datn.trip_service.controller;

import com.datn.trip_service.dto.AdventureResponse;
import com.datn.trip_service.dto.CreateTripRequest;
import com.datn.trip_service.dto.TripResponse;
import com.datn.trip_service.model.Trip;
import com.datn.trip_service.model.User;
import com.datn.trip_service.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@RequestBody CreateTripRequest request) {
        try {
            Trip trip = tripService.createTrip(request);
            TripResponse response = new TripResponse(true, "Trip created successfully", trip);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            TripResponse response = new TripResponse(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable String id) {
        try {
            Trip trip = tripService.getTripById(id);
            TripResponse response = new TripResponse(true, "Trip retrieved successfully", trip);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TripResponse response = new TripResponse(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trip>> getTripsByUserId(@PathVariable String userId) {
        try {
            List<Trip> trips = tripService.getTripsByUserId(userId);
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/member/{userId}")
    public ResponseEntity<List<Trip>> getTripsByMemberId(@PathVariable String userId) {
        try {
            List<Trip> trips = tripService.getTripsByMemberId(userId);
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(@PathVariable String id, @RequestBody CreateTripRequest request) {
        try {
            Trip trip = tripService.updateTrip(id, request);
            TripResponse response = new TripResponse(true, "Trip updated successfully", trip);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TripResponse response = new TripResponse(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable String id) {
        try {
            tripService.deleteTrip(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    // Add member to trip
    @PostMapping("/{tripId}/members")
    public ResponseEntity<TripResponse> addMember(@PathVariable String tripId, @RequestBody User member) {
        try {
            Trip trip = tripService.addMember(tripId, member);
            TripResponse response = new TripResponse(true, "Member added successfully", trip);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TripResponse response = new TripResponse(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Remove member from trip
    @DeleteMapping("/{tripId}/members/{userId}")
    public ResponseEntity<TripResponse> removeMember(@PathVariable String tripId, @PathVariable String userId) {
        try {
            Trip trip = tripService.removeMember(tripId, userId);
            TripResponse response = new TripResponse(true, "Member removed successfully", trip);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TripResponse response = new TripResponse(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Update shared users list
    @PutMapping("/{tripId}/shared-users")
    public ResponseEntity<TripResponse> updateSharedUsers(
            @PathVariable String tripId, 
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") String userId) {
        try {
            @SuppressWarnings("unchecked")
            List<String> sharedWithUserIds = (List<String>) request.get("sharedWithUserIds");
            Trip trip = tripService.updateSharedUsers(tripId, sharedWithUserIds);
            TripResponse response = new TripResponse(true, "Shared users updated successfully", trip);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TripResponse response = new TripResponse(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Check if user is member
    @GetMapping("/{tripId}/members/{userId}/check")
    public ResponseEntity<Map<String, Boolean>> checkMembership(
            @PathVariable String tripId, 
            @PathVariable String userId) {
        try {
            boolean isMember = tripService.isMember(tripId, userId);
            return ResponseEntity.ok(Map.of("isMember", isMember));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    // Check if user can view trip
    @PostMapping("/{tripId}/check-access")
    public ResponseEntity<Map<String, Boolean>> checkAccess(
            @PathVariable String tripId,
            @RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            @SuppressWarnings("unchecked")
            List<String> followerIds = (List<String>) request.get("followerIds");
            
            boolean canView = tripService.canViewTrip(tripId, userId, followerIds);
            return ResponseEntity.ok(Map.of("canView", canView));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    /**
     * Adventure endpoint - returns all trips with full details in 1 API call
     * Replaces 11 separate calls (1 discover + 10 trip/user details)
     * 
     * Usage: GET /api/trips/adventure?userId=xxx&limit=10
     */
    @GetMapping("/adventure")
    public ResponseEntity<AdventureResponse> getAdventureTrips(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            AdventureResponse response = tripService.getAdventureTrips(userId, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getAdventureTrips endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
