package com.datn.trip_service.model.plan;

import com.datn.trip_service.model.Plan;
import java.time.LocalDateTime;

public class LodgingPlan extends Plan {
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String phone;
    
    public LodgingPlan() {
        super();
    }
    
    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }
    
    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }
    
    public LocalDateTime getCheckOutDate() {
        return checkOutDate;
    }
    
    public void setCheckOutDate(LocalDateTime checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
}