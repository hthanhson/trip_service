package com.datn.trip_service.model.plan;

import com.datn.trip_service.model.Plan;
import java.time.LocalDateTime;

public class FlightPlan extends Plan {
    private String arrivalLocation;
    private String arrivalAddress;
    private LocalDateTime arrivalDate;
    
    public FlightPlan() {
        super();
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
    
    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }
    
    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
}
