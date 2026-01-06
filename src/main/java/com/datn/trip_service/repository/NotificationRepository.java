package com.datn.trip_service.repository;

import com.datn.trip_service.model.Notification;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class NotificationRepository {

    private static final String COLLECTION_NOTIFICATIONS = "notifications";
    private static final String COLLECTION_USER_DEVICE = "user_device";

    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    public void saveNotification(Notification notification) throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("id", notification.getId());
        notificationData.put("userId", notification.getUserId());
        notificationData.put("title", notification.getTitle());
        notificationData.put("message", notification.getMessage());
        notificationData.put("type", notification.getType());
        notificationData.put("timestamp", notification.getTimestamp());
        notificationData.put("isRead", notification.getIsRead());
        
        if (notification.getTripId() != null) {
            notificationData.put("tripId", notification.getTripId());
        }
        if (notification.getTripTitle() != null) {
            notificationData.put("tripTitle", notification.getTripTitle());
        }

        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NOTIFICATIONS)
                .document(notification.getId())
                .set(notificationData);

        future.get(); // Wait for completion
        System.out.println(" Saved notification to Firestore: " + notification.getId());
    }

    public List<Notification> getNotificationsByUserId(String userId) throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();

        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NOTIFICATIONS)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();

        QuerySnapshot querySnapshot = future.get();
        List<Notification> notifications = new ArrayList<>();

        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            Notification notification = new Notification();
            notification.setId(document.getString("id"));
            notification.setUserId(document.getString("userId"));
            notification.setTitle(document.getString("title"));
            notification.setMessage(document.getString("message"));
            notification.setType(document.getString("type"));
            notification.setTimestamp(document.getLong("timestamp"));
            notification.setIsRead(document.getBoolean("isRead"));
            notification.setTripId(document.getString("tripId"));
            notification.setTripTitle(document.getString("tripTitle"));
            
            notifications.add(notification);
        }

        return notifications;
    }

    public void markAsRead(String notificationId) throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();

        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NOTIFICATIONS)
                .document(notificationId)
                .update("isRead", true);

        future.get();
        System.out.println(" Marked notification as read: " + notificationId);
    }

    public void deleteNotification(String notificationId) throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();

        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NOTIFICATIONS)
                .document(notificationId)
                .delete();

        future.get();
        System.out.println(" Deleted notification: " + notificationId);
    }

    public void saveFcmToken(String userId, String fcmToken) throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();
        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("userId", userId);
        deviceData.put("fcmToken", fcmToken);
        deviceData.put("notificationsEnabled", true);
        deviceData.put("lastUpdated", System.currentTimeMillis());


        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_USER_DEVICE)
                .document(userId)
                .set(deviceData, SetOptions.merge());

        WriteResult result = future.get();
    }

    public Map<String, Object> getUserDevice(String userId) throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();

        ApiFuture<DocumentSnapshot> future = firestore.collection(COLLECTION_USER_DEVICE)
                .document(userId)
                .get();

        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.getData();
        }
        return null;
    }

    public void updateNotificationSettings(String userId, boolean enabled) throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();

        Map<String, Object> updates = new HashMap<>();
        updates.put("notificationsEnabled", enabled);
        updates.put("lastUpdated", System.currentTimeMillis());

        // Use set() with merge option to create document if it doesn't exist
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_USER_DEVICE)
                .document(userId)
                .set(updates, SetOptions.merge());

        future.get();
        System.out.println("Updated notification settings for user: " + userId);
    }

    public int getUnreadCount(String userId) throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();

        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NOTIFICATIONS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isRead", false)
                .get();

        QuerySnapshot querySnapshot = future.get();
        return querySnapshot.size();
    }

    public List<String> getUsersWithNotificationsEnabled() throws ExecutionException, InterruptedException {
        Firestore firestore = getFirestore();

        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_USER_DEVICE)
                .whereEqualTo("notificationsEnabled", true)
                .get();

        QuerySnapshot querySnapshot = future.get();
        List<String> userIds = new ArrayList<>();

        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            String userId = document.getString("userId");
            if (userId != null) {
                userIds.add(userId);
            }
        }

        return userIds;
    }
}
