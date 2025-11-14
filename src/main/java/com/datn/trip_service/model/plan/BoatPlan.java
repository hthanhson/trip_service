package com.datn.trip_service.model.plan;

import com.datn.trip_service.model.Plan;
import java.time.LocalDateTime;

public class BoatPlan extends Plan {
    private LocalDateTime arrivalTime;
    private String arrivalLocation;
    private String arrivalAddress;
    
    public BoatPlan() {
        super();
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public String getArrivalLocation() {
        return arrivalLocation;
    }
    
    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }
    
    public String getArrivalAddress() {
        return arrivalAddress;
    }
    
    public void setArrivalAddress(String arrivalAddress) {
        this.arrivalAddress = arrivalAddress;
    }
}
