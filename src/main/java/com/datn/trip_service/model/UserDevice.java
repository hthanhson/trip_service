package com.datn.trip_service.model;

public class UserDevice {
    private String userId;
    private String fcmToken;
    private Boolean notificationsEnabled;
    private Long lastUpdated;

    public UserDevice() {
    }

    public UserDevice(String userId, String fcmToken, Boolean notificationsEnabled, Long lastUpdated) {
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.notificationsEnabled = notificationsEnabled;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
