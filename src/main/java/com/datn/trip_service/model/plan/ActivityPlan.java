package com.datn.trip_service.model.plan;

import com.datn.trip_service.model.Plan;
import java.time.LocalDateTime;

public class ActivityPlan extends Plan {
    private LocalDateTime endTime;
    
    public ActivityPlan() {
        super();
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}