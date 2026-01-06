package com.datn.trip_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    private boolean firebaseInitialized = false;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase...");
                
                InputStream serviceAccount = new ClassPathResource("firebase-service.json").getInputStream();
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://social-104a1-default-rtdb.firebaseio.com")
                        .setProjectId("social-104a1")
                        .setStorageBucket("social-104a1.appspot.com")
                        .build();

                FirebaseApp.initializeApp(options);
                firebaseInitialized = true;
                
                logger.info("✓ Firebase initialized successfully!");
                logger.info("  - Project ID: social-104a1");
                logger.info("  - Database URL: https://social-104a1-default-rtdb.firebaseio.com");
                logger.info("  - Storage Bucket: social-104a1.appspot.com");
            } else {
                logger.info("Firebase already initialized");
                firebaseInitialized = true;
            }
        } catch (IOException e) {
            logger.error("✗ Firebase initialization failed - {}", e.getMessage());
            logger.error("  Service will continue without Firebase integration.");
            logger.error("  To enable Firebase:");
            logger.error("    1. Download service account key from Firebase Console");
            logger.error("    2. Place it at: src/main/resources/firebase-service.json");
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("✗ Unexpected error during Firebase initialization", e);
        }
    }
    
    @Bean
    public Firestore firestore() {
        if (!firebaseInitialized) {
            logger.warn("Firebase not initialized - Firestore bean may not work properly");
        }
        try {
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            logger.error("Failed to get Firestore instance: {}", e.getMessage());
            throw e;
        }
    }
}
