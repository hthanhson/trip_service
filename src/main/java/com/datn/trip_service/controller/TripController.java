package com.datn.trip_service.controller;

import com.datn.trip_service.dto.CreateTripRequest;
import com.datn.trip_service.dto.TripResponse;
import com.datn.trip_service.model.Trip;
import com.datn.trip_service.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
