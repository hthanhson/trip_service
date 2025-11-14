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
            @RequestBody CreateFlightPlanRequest request) {
        request.setTripId(tripId);
        Plan createdPlan = planService.createFlightPlan(request);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }
    
    @PostMapping("/restaurant")
    public ResponseEntity<Plan> createRestaurantPlan(
            @PathVariable String tripId,
            @RequestBody CreateRestaurantPlanRequest request) {
        request.setTripId(tripId);
        Plan createdPlan = planService.createRestaurantPlan(request);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }
    
    @PostMapping("/lodging")
    public ResponseEntity<Plan> createLodgingPlan(
            @PathVariable String tripId,
            @RequestBody CreateLodgingPlanRequest request) {
        request.setTripId(tripId);
        Plan createdPlan = planService.createLodgingPlan(request);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }
    
    @PostMapping("/activity")
    public ResponseEntity<Plan> createActivityPlan(
            @PathVariable String tripId,
            @RequestBody CreateActivityPlanRequest request) {
        request.setTripId(tripId);
        Plan createdPlan = planService.createActivityPlan(request);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }
    
    @PostMapping("/boat")
    public ResponseEntity<Plan> createBoatPlan(
            @PathVariable String tripId,
            @RequestBody CreateBoatPlanRequest request) {
        request.setTripId(tripId);
        Plan createdPlan = planService.createBoatPlan(request);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }
    
    @PostMapping("/car-rental")
    public ResponseEntity<Plan> createCarRentalPlan(
            @PathVariable String tripId,
            @RequestBody CreateCarRentalPlanRequest request) {
        request.setTripId(tripId);
        Plan createdPlan = planService.createCarRentalPlan(request);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
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
            @RequestBody CreatePlanRequest request) {
        request.setTripId(tripId);
        Plan updatedPlan = planService.updatePlan(planId, request);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(@PathVariable String planId) {
        planService.deletePlan(planId);
        return ResponseEntity.noContent().build();
    }
}
