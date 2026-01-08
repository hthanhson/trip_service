package com.datn.trip_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initFirebase() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                logger.info("Firebase already initialized");
                return;
            }

            String firebaseJson = System.getenv("firebase-service");

            if (firebaseJson == null || firebaseJson.isBlank()) {
                throw new IllegalStateException(
                        "FIREBASE_SERVICE_ACCOUNT env variable not found"
                );
            }

            ByteArrayInputStream serviceAccount =
                    new ByteArrayInputStream(firebaseJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            logger.info("✓ Firebase initialized successfully");

        } catch (Exception e) {
            logger.error("✗ Firebase initialization failed", e);
        }
    }

    @Bean
    public Firestore firestore() {
        if (FirebaseApp.getApps().isEmpty()) {
            throw new IllegalStateException(
                    "FirebaseApp is not initialized. Firestore unavailable."
            );
        }
        return FirestoreClient.getFirestore();
    }
}
