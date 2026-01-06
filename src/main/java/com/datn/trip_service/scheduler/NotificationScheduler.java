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

    @Scheduled(cron = "0 15 12 * * ?", zone = "UTC")
    public void sendDailyTripNotification() {
        try {
            LocalDate today = LocalDate.now();

            List<Trip> activeTrips = tripRepository.findActiveTrips(today);
            
            if (!activeTrips.isEmpty()) {
                System.out.println("⏸There are " + activeTrips.size() + " active trip(s). No notification will be sent.");
                for (Trip trip : activeTrips) {
                    System.out.println("  - Active: " + trip.getTitle() + " (" + trip.getStartDate() + " to " + trip.getEndDate() + ")");
                }
                return;
            }
            
            System.out.println(" No active trips. Checking for upcoming trips...");

            List<Trip> upcomingTrips = tripRepository.findUpcomingTrips(today);
            
            if (upcomingTrips.isEmpty()) {
                System.out.println("No trips in the future. No notification will be sent.");
                return;
            }

            Trip nextTrip = upcomingTrips.get(0);
            System.out.println("Sending notification for upcoming trip: " + nextTrip.getTitle() + " (starts " + nextTrip.getStartDate() + ")");
            sendUpcomingTripNotification(nextTrip);
            
        } catch (Exception e) {
            System.err.println("Error in sendDailyTripNotification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendUpcomingTripNotification(Trip trip) {
        try {
            String title = "Chuyến đi sắp diễn ra!";
            String body = String.format(
                    "Chuyến đi '%s' của bạn sẽ bắt đầu vào ngày %s. Hãy chuẩn bị hành lý nhé!",
                    trip.getTitle(),
                    trip.getStartDate().format(dateFormatter)
            );


            // Prepare data payload
            Map<String, String> data = new HashMap<>();
            data.put("type", "TRIP_REMINDER");
            data.put("tripId", trip.getId());
            data.put("tripTitle", trip.getTitle());
            data.put("startDate", trip.getStartDate().toString());
            if (trip.getEndDate() != null) {
                data.put("endDate", trip.getEndDate().toString());
            }
            if (trip.getCoverPhoto() != null) {
                data.put("coverPhoto", trip.getCoverPhoto());
            }

            notificationService.sendTopicNotification(
                    "trip-reminders",
                    title,
                    body,
                    data
            );


            System.out.println("Sent notification for trip: " + trip.getTitle());


        } catch (Exception e) {
            System.err.println("Error sending trip notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
