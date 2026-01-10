package com.datn.trip_service.scheduler;

import com.datn.trip_service.model.Trip;
import com.datn.trip_service.repository.TripRepository;
import com.datn.trip_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component

public class NotificationScheduler {
    @Autowired
    private TripRepository tripRepository;


    @Autowired
    private NotificationService notificationService;


    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Scheduled(cron = "0 38 7 * * ?", zone = "UTC")
    public void sendDailyTripNotification() {
        try {
            LocalDate today = LocalDate.now();

            List<String> allUserIds = tripRepository.getAllUserIds();

            int notificationsSent = 0;
            
            for (String userId : allUserIds) {

                List<Trip> activeTrips = tripRepository.findActiveTripsForUser(userId, today);
                
                if (!activeTrips.isEmpty()) {
                    for (Trip trip : activeTrips) {
                        System.out.println("  - Active: " + trip.getTitle() + " (" + trip.getStartDate() + " to " + trip.getEndDate() + ")");
                    }
                    continue;
                }

                // User has no active trips - check for upcoming trips
                List<Trip> upcomingTrips = tripRepository.findUpcomingTripsForUser(userId, today);
                
                if (upcomingTrips.isEmpty()) {
                    continue;
                }
                
                // Has upcoming trip - send notification
                Trip nextTrip = upcomingTrips.get(0);
                System.out.println(" Sending notification for upcoming trip: " + nextTrip.getTitle() + " (starts " + nextTrip.getStartDate() + ")");
                String response = sendUpcomingTripNotification(userId, nextTrip);
                
                // Only count as sent if successful
                if (response != null) {
                    notificationsSent++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendUpcomingTripNotification(String userId, Trip trip) {
        try {
            String title = "Chuyến đi sắp diễn ra!";
            String body = String.format(
                    "Chuyến đi '%s' của bạn sẽ bắt đầu vào ngày %s. Hãy chuẩn bị hành lý nhé!",
                    trip.getTitle(),
                    trip.getStartDate().format(dateFormatter)
            );


            // Prepare data payload
            Map<String, String> data = new HashMap<>();
            data.put("type", "TRIP");
            data.put("tripId", trip.getId());
            data.put("tripTitle", trip.getTitle());
            data.put("startDate", trip.getStartDate().toString());
            if (trip.getEndDate() != null) {
                data.put("endDate", trip.getEndDate().toString());
            }
            if (trip.getCoverPhoto() != null) {
                data.put("coverPhoto", trip.getCoverPhoto());
            }

            // Send notification to specific user
            String response = notificationService.sendNotificationToUser(userId, title, body, data);
            
            if (response != null) {
                System.out.println("  Notification sent successfully for trip: " + trip.getTitle());
            } else {
                System.out.println("  Failed to send notification for trip: " + trip.getTitle());
            }
            
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
