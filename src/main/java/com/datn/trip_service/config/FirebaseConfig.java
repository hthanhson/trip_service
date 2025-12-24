package com.datn.trip_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new ClassPathResource("firebase-service.json").getInputStream();
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://apptravel-addd6-default-rtdb.firebaseio.com")
                        .setProjectId("apptravel-addd6")
                        .setStorageBucket("apptravel-addd6.firebasestorage.app")
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println(" Firebase initialized successfully!");
                System.out.println("Project: apptravel-addd6");
                System.out.println("Database: https://apptravel-addd6-default-rtdb.firebaseio.com");
            }
        } catch (IOException e) {
            System.err.println(" Warning: Firebase initialization failed - " + e.getMessage());
            System.err.println(" Service will continue without Firebase integration.");
            System.err.println(" To enable Firebase:");
            System.err.println("   1. Download service account key from Firebase Console");
            System.err.println("   2. Place it at: src/main/resources/firebase-service-account.json");
        }
    }
    
    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}
