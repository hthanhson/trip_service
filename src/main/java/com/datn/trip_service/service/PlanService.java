package com.datn.trip_service.service;

import com.datn.trip_service.dto.*;
import com.datn.trip_service.model.Plan;
import com.datn.trip_service.model.PlanType;
import com.datn.trip_service.model.plan.*;
import com.datn.trip_service.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

//    public Plan createPlan(CreatePlanRequest request) {
//        Plan plan = new Plan();
//        plan.setTripId(request.getTripId());
//        plan.setTitle(request.getTitle());
//        plan.setAddress(request.getAddress());
//        plan.setLocation(request.getLocation());
//
//        // Parse date-time strings
//        if (request.getStartTime() != null) {
//            plan.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
//        }
//
//        plan.setExpense(request.getExpense());
//        plan.setPhotoUrl(request.getPhotoUrl());
//
//        // Parse PlanType enum
//        if (request.getType() != null) {
//            plan.setType(PlanType.valueOf(request.getType()));
//        }
//
//        return planRepository.save(plan);
//    }
    
    public Plan createFlightPlan(CreateFlightPlanRequest request) {
        FlightPlan plan = new FlightPlan();
        plan.setTripId(request.getTripId());
        plan.setTitle(request.getTitle());
        plan.setAddress(request.getAddress());
        plan.setLocation(request.getLocation());
        
        if (request.getStartTime() != null) {
            plan.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        }
        
        plan.setExpense(request.getExpense());
        plan.setPhotoUrl(request.getPhotoUrl());
        plan.setType(PlanType.FLIGHT);
        
        // Flight-specific fields
        plan.setArrivalLocation(request.getArrivalLocation());
        plan.setArrivalAddress(request.getArrivalAddress());
        if (request.getArrivalDate() != null) {
            plan.setArrivalDate(LocalDateTime.parse(request.getArrivalDate(), formatter));
        }
        
        return planRepository.save(plan);
    }
    
    public Plan createRestaurantPlan(CreateRestaurantPlanRequest request) {
        RestaurantPlan plan = new RestaurantPlan();
        plan.setTripId(request.getTripId());
        plan.setTitle(request.getTitle());
        plan.setAddress(request.getAddress());
        plan.setLocation(request.getLocation());
        
        if (request.getStartTime() != null) {
            plan.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        }
        
        plan.setExpense(request.getExpense());
        plan.setPhotoUrl(request.getPhotoUrl());
        plan.setType(PlanType.RESTAURANT);
        
        // Restaurant-specific fields
        if (request.getReservationDate() != null) {
            plan.setReservationDate(LocalDateTime.parse(request.getReservationDate(), formatter));
        }
        if (request.getReservationTime() != null) {
            plan.setReservationTime(LocalDateTime.parse(request.getReservationTime(), formatter));
        }
        
        return planRepository.save(plan);
    }
    
    public Plan createLodgingPlan(CreateLodgingPlanRequest request) {
        LodgingPlan plan = new LodgingPlan();
        plan.setTripId(request.getTripId());
        plan.setTitle(request.getTitle());
        plan.setAddress(request.getAddress());
        plan.setLocation(request.getLocation());
        
        if (request.getStartTime() != null) {
            plan.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        }
        
        plan.setExpense(request.getExpense());
        plan.setPhotoUrl(request.getPhotoUrl());
        plan.setType(PlanType.LODGING);
        
        // Lodging-specific fields
        if (request.getCheckInDate() != null) {
            plan.setCheckInDate(LocalDateTime.parse(request.getCheckInDate(), formatter));
        }
        if (request.getCheckOutDate() != null) {
            plan.setCheckOutDate(LocalDateTime.parse(request.getCheckOutDate(), formatter));
        }
        plan.setPhone(request.getPhone());
        
        return planRepository.save(plan);
    }
    
    public Plan createActivityPlan(CreateActivityPlanRequest request) {
        ActivityPlan plan = new ActivityPlan();
        plan.setTripId(request.getTripId());
        plan.setTitle(request.getTitle());
        plan.setAddress(request.getAddress());
        plan.setLocation(request.getLocation());
        
        if (request.getStartTime() != null) {
            plan.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        }
        if (request.getEndTime() != null) {
            plan.setEndTime(LocalDateTime.parse(request.getEndTime(), formatter));
        }
        
        plan.setExpense(request.getExpense());
        plan.setPhotoUrl(request.getPhotoUrl());
        plan.setType(PlanType.ACTIVITY);
        
        return planRepository.save(plan);
    }
    
    public Plan createBoatPlan(CreateBoatPlanRequest request) {
        BoatPlan plan = new BoatPlan();
        plan.setTripId(request.getTripId());
        plan.setTitle(request.getTitle());
        plan.setAddress(request.getAddress());
        plan.setLocation(request.getLocation());
        
        if (request.getStartTime() != null) {
            plan.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        }
        
        plan.setExpense(request.getExpense());
        plan.setPhotoUrl(request.getPhotoUrl());
        plan.setType(PlanType.BOAT);
        
        // Boat-specific fields
        if (request.getArrivalTime() != null) {
            plan.setArrivalTime(LocalDateTime.parse(request.getArrivalTime(), formatter));
        }
        plan.setArrivalLocation(request.getArrivalLocation());
        plan.setArrivalAddress(request.getArrivalAddress());
        
        return planRepository.save(plan);
    }
    
    public Plan createCarRentalPlan(CreateCarRentalPlanRequest request) {
        CarRentalPlan plan = new CarRentalPlan();
        plan.setTripId(request.getTripId());
        plan.setTitle(request.getTitle());
        plan.setAddress(request.getAddress());
        plan.setLocation(request.getLocation());
        
        if (request.getStartTime() != null) {
            plan.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        }
        
        plan.setExpense(request.getExpense());
        plan.setPhotoUrl(request.getPhotoUrl());
        plan.setType(PlanType.CAR_RENTAL);
        
        // CarRental-specific fields
        if (request.getPickupDate() != null) {
            plan.setPickupDate(LocalDateTime.parse(request.getPickupDate(), formatter));
        }
        if (request.getPickupTime() != null) {
            plan.setPickupTime(LocalDateTime.parse(request.getPickupTime(), formatter));
        }
        plan.setPhone(request.getPhone());
        
        return planRepository.save(plan);
    }

    public Plan getPlanById(String id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));
    }

    public List<Plan> getPlansByTripId(String tripId) {
        return planRepository.findByTripId(tripId);
    }

    public Plan updatePlan(String id, CreatePlanRequest request) {
        Plan plan = getPlanById(id);
        
        plan.setTitle(request.getTitle());
        plan.setAddress(request.getAddress());
        plan.setLocation(request.getLocation());
        
        if (request.getStartTime() != null) {
            plan.setStartTime(LocalDateTime.parse(request.getStartTime(), formatter));
        }

        plan.setExpense(request.getExpense());
        plan.setPhotoUrl(request.getPhotoUrl());
        
        if (request.getType() != null) {
            plan.setType(PlanType.valueOf(request.getType()));
        }
        
        return planRepository.save(plan);
    }

    public void deletePlan(String id) {
        planRepository.deleteById(id);
    }
}
