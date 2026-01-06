package com.datn.trip_service.service;

import com.datn.trip_service.dto.NotificationRequest;
import com.datn.trip_service.model.Notification;
import com.datn.trip_service.repository.NotificationRepository;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public String sendNotification(NotificationRequest request) {
        try {
            // Build the notification
            com.google.firebase.messaging.Notification notification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getBody())
                    .setImage(request.getImage())
                    .build();

            // Build the message with notification and data payload
            Message.Builder messageBuilder = Message.builder()
                    .setToken(request.getToken())
                    .setNotification(notification);

            // Add data payload if present
            if (request.getData() != null && !request.getData().isEmpty()) {
                messageBuilder.putAllData(request.getData());
            }

            // Add Android-specific configuration
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setColor("#FF6B35")
                            .build())
                    .build());

            Message message = messageBuilder.build();

            // Send the message
            String response = FirebaseMessaging.getInstance().send(message);

            System.out.println("Successfully sent message: " + response);
            return response;

        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public BatchResponse sendMulticastNotification(
            java.util.List<String> tokens,
            String title,
            String body,
            Map<String, String> data) {
        try {
            // Build the notification
            com.google.firebase.messaging.Notification notification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Build multicast message
            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .setNotification(notification)
                    .addAllTokens(tokens);

            // Add data payload if present
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            // Add Android configuration
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setColor("#FF6B35")
                            .build())
                    .build());

            MulticastMessage message = messageBuilder.build();

            // Send to multiple devices
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            System.out.println(" Successfully sent " + response.getSuccessCount() + " messages");
            if (response.getFailureCount() > 0) {
                System.err.println(" Failed to send " + response.getFailureCount() + " messages");
            }

            return response;

        } catch (FirebaseMessagingException e) {
            System.err.println(" Error sending multicast notification: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String sendTopicNotification(String topic, String title, String body, Map<String, String> data) {
        try {
            // Build the notification
            com.google.firebase.messaging.Notification notification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Build the message
            Message.Builder messageBuilder = Message.builder()
                    .setTopic(topic)
                    .setNotification(notification);

            // Add data payload if present
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            // Add Android configuration
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setColor("#FF6B35")
                            .build())
                    .build());

            Message message = messageBuilder.build();

            // Send the message
            String response = FirebaseMessaging.getInstance().send(message);

            System.out.println("Successfully sent topic message: " + response);

            // Save notification to Firestore for all users with notifications enabled
            saveNotificationForEnabledUsers(title, body, data);

            return response;

        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending topic notification: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    private void saveNotificationForEnabledUsers(String title, String body, Map<String, String> data) {
        try {
            List<String> userIds = notificationRepository.getUsersWithNotificationsEnabled();

            for (String userId : userIds) {
                Notification notification = new Notification();
                notification.setId(UUID.randomUUID().toString());
                notification.setUserId(userId);
                notification.setTitle(title);
                notification.setMessage(body);
                notification.setType(data != null ? data.getOrDefault("type", "GENERAL") : "GENERAL");
                notification.setTimestamp(System.currentTimeMillis());
                notification.setIsRead(false);

                if (data != null) {
                    notification.setTripId(data.get("tripId"));
                    notification.setTripTitle(data.get("tripTitle"));
                }

                notificationRepository.saveNotification(notification);
            }

            System.out.println(" Saved notification to Firestore for " + userIds.size() + " users");
        } catch (Exception e) {
            System.err.println(" Error saving notification to Firestore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveNotificationForUser(String userId, String title, String body, String type, Map<String, String> additionalData) {
        try {
            Notification notification = new Notification();
            notification.setId(UUID.randomUUID().toString());
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setMessage(body);
            notification.setType(type);
            notification.setTimestamp(System.currentTimeMillis());
            notification.setIsRead(false);

            if (additionalData != null) {
                notification.setTripId(additionalData.get("tripId"));
                notification.setTripTitle(additionalData.get("tripTitle"));
            }

            notificationRepository.saveNotification(notification);
            System.out.println(" Saved notification for user: " + userId);
        } catch (Exception e) {
            System.err.println(" Error saving notification for user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save FCM token for user
     */
    public void saveFcmToken(String userId, String fcmToken) {
        try {
            notificationRepository.saveFcmToken(userId, fcmToken);

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException("Failed to save FCM token", e);
        }
    }


    public void updateNotificationSettings(String userId, boolean enabled) {
        try {
            notificationRepository.updateNotificationSettings(userId, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String sendNotificationToUser(String userId, String title, String body, Map<String, String> data) {
        try {
            Map<String, Object> userDevice = notificationRepository.getUserDevice(userId);
            
            if (userDevice == null) {
                try {
                    notificationRepository.updateNotificationSettings(userId, true);
                    System.out.println("Created default notification settings for user: " + userId);
                } catch (Exception e) {
                    System.err.println(" Failed to create default settings: " + e.getMessage());
                }
                return null;
            }

            String fcmToken = (String) userDevice.get("fcmToken");
            if (fcmToken == null || fcmToken.isEmpty()) {
                return null;
            }

            // Check if notifications are enabled for this user
            Boolean notificationsEnabled = (Boolean) userDevice.get("notificationsEnabled");
            if (notificationsEnabled != null && !notificationsEnabled) {
                return null;
            }

            // Build the notification
            com.google.firebase.messaging.Notification notification = com.google.firebase.messaging.Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification);

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setColor("#FF6B35")
                            .build())
                    .build());

            Message message = messageBuilder.build();

            // Send the message with error handling for invalid tokens
            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("✅ Successfully sent FCM notification to user " + userId + ": " + response);
            } catch (com.google.firebase.messaging.FirebaseMessagingException e) {
                // Check if error is due to invalid/unregistered token
                if (e.getMessagingErrorCode() == com.google.firebase.messaging.MessagingErrorCode.UNREGISTERED) {
                    System.err.println("⚠️ User " + userId + " has invalid/unregistered FCM token. Skipping notification send.");
                    // Could delete the token from user_device here if needed
                    return null;
                } else {
                    // Re-throw other FCM errors
                    throw e;
                }
            }

            System.out.println("✅ Successfully sent FCM notification to user " + userId);
            
            // Save notification to Firestore collection
            try {
                com.datn.trip_service.model.Notification notificationRecord = new com.datn.trip_service.model.Notification();
                notificationRecord.setId(String.valueOf(System.currentTimeMillis()));
                notificationRecord.setUserId(userId);
                notificationRecord.setTitle(title);
                notificationRecord.setMessage(body);
                notificationRecord.setType(data != null ? data.getOrDefault("type", "GENERAL") : "GENERAL");
                notificationRecord.setTimestamp(System.currentTimeMillis());
                notificationRecord.setIsRead(false);
                
                notificationRepository.saveNotification(notificationRecord);
                System.out.println("✅ Notification saved to Firestore: " + title);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to save notification to Firestore: " + e.getMessage());
                // Don't fail the whole operation just because saving to Firestore failed
            }
            
            return userId;

        } catch (Exception e) {
            System.err.println("❌ Error sending notification to user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}