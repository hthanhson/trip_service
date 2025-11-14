package com.datn.trip_service.model.plan;

import com.datn.trip_service.model.Plan;
import java.time.LocalDateTime;

public class RestaurantPlan extends Plan {
    private LocalDateTime reservationDate;
    private LocalDateTime reservationTime;
    
    public RestaurantPlan() {
        super();
    }
    
    public LocalDateTime getReservationDate() {
        return reservationDate;
    }
    
    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }
    
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }
    
    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }
}
