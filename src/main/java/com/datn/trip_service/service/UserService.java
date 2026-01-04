package com.datn.trip_service.service;

import com.datn.trip_service.model.User;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    @Autowired
    private Firestore firestore;

    public User getUserById(String userId) throws ExecutionException, InterruptedException {
        try {
            DocumentSnapshot userDoc = firestore.collection("users")
                    .document(userId)
                    .get()
                    .get();
            
            if (userDoc.exists()) {
                return User.builder()
                        .id(userDoc.getId())
                        .firstName(userDoc.getString("firstName"))
                        .lastName(userDoc.getString("lastName"))
                        .email(userDoc.getString("email"))
                        .profilePicture(userDoc.getString("profilePicture"))
                        .role(userDoc.getString("role"))
                        .enabled(userDoc.getBoolean("enabled"))
                        .build();
            } else {
                throw new RuntimeException("User not found with id: " + userId);
            }
        } catch (Exception e) {
            System.err.println("Error in getUserById: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get user: " + e.getMessage());
        }
    }

    public List<User> getFollowers(String userId) throws ExecutionException, InterruptedException {
        List<User> followers = new ArrayList<>();
        
        try {
            QuerySnapshot followingSnapshot = firestore.collection("follows")
                    .whereEqualTo("followerID", userId)
                    .get()
                    .get();

            for (DocumentSnapshot doc : followingSnapshot.getDocuments()) {
                String followedUserId = doc.getString("followingID");
                
                if (followedUserId == null || followedUserId.isEmpty()) {
                    continue;
                }
                
                // Get user details from users collection
                DocumentSnapshot userDoc = firestore.collection("users")
                        .document(followedUserId)
                        .get()
                        .get();
                
                if (userDoc.exists()) {
                    // Manually extract fields to avoid LocalDateTime deserialization issue
                    User user = User.builder()
                            .id(userDoc.getId())
                            .firstName(userDoc.getString("firstName"))
                            .lastName(userDoc.getString("lastName"))
                            .email(userDoc.getString("email"))
                            .profilePicture(userDoc.getString("profilePicture"))
                            .role(userDoc.getString("role"))
                            .enabled(userDoc.getBoolean("enabled"))
                            // Skip createdAt and updatedAt to avoid conversion issues
                            .build();
                    
                    followers.add(user);
                    System.out.println("Added user: " + user.getFirstName() + " " + user.getLastName());
                } else {
                    System.out.println("User document not found for ID: " + followedUserId);
                }
            }

        } catch (Exception e) {
            System.err.println("Error in getFollowers: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get followers: " + e.getMessage());
        }
        
        return followers;
    }
}
