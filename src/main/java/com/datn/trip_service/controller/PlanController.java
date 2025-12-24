package com.datn.trip_service.controller;

import com.datn.trip_service.dto.*;
import com.datn.trip_service.model.Plan;
import com.datn.trip_service.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/plans")
@CrossOrigin(origins = "*")
public class PlanController {

    @Autowired
    private PlanService planService;

    
    @PostMapping("/flight")
    public ResponseEntity<Plan> createFlightPlan(
            @PathVariable String tripId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateFlightPlanRequest request) {
        try {
            request.setTripId(tripId);
            Plan createdPlan = planService.createFlightPlanWithAuth(request, userId);
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    
    @PostMapping("/restaurant")
    public ResponseEntity<Plan> createRestaurantPlan(
            @PathVariable String tripId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateRestaurantPlanRequest request) {
        try {
            request.setTripId(tripId);
            Plan createdPlan = planService.createRestaurantPlanWithAuth(request, userId);
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    
    @PostMapping("/lodging")
    public ResponseEntity<Plan> createLodgingPlan(
            @PathVariable String tripId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateLodgingPlanRequest request) {
        try {
            request.setTripId(tripId);
            Plan createdPlan = planService.createLodgingPlanWithAuth(request, userId);
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    
    @PostMapping("/activity")
    public ResponseEntity<Plan> createActivityPlan(
            @PathVariable String tripId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateActivityPlanRequest request) {
        try {
            request.setTripId(tripId);
            Plan createdPlan = planService.createActivityPlanWithAuth(request, userId);
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    
    @PostMapping("/boat")
    public ResponseEntity<Plan> createBoatPlan(
            @PathVariable String tripId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateBoatPlanRequest request) {
        try {
            request.setTripId(tripId);
            Plan createdPlan = planService.createBoatPlanWithAuth(request, userId);
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
    
    @PostMapping("/car-rental")
    public ResponseEntity<Plan> createCarRentalPlan(
            @PathVariable String tripId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreateCarRentalPlanRequest request) {
        try {
            request.setTripId(tripId);
            Plan createdPlan = planService.createCarRentalPlanWithAuth(request, userId);
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Plan>> getPlansByTripId(@PathVariable String tripId) {
        List<Plan> plans = planService.getPlansByTripId(tripId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<Plan> getPlanById(@PathVariable String planId) {
        Plan plan = planService.getPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<Plan> updatePlan(
            @PathVariable String tripId,
            @PathVariable String planId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CreatePlanRequest request) {
        try {
            request.setTripId(tripId);
            Plan updatedPlan = planService.updatePlanWithAuth(planId, request, userId);
            return ResponseEntity.ok(updatedPlan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable String planId,
            @RequestHeader("X-User-Id") String userId) {
        try {
            planService.deletePlanWithAuth(planId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    @DeleteMapping("/{planId}/photos/{photoFileName}")
    public ResponseEntity<Void> deletePhotoFromPlan(
            @PathVariable String tripId,
            @PathVariable String planId,
            @PathVariable String photoFileName) {
        planService.deletePhotoFromPlan(planId, photoFileName);
        return ResponseEntity.noContent().build();
    }
}
