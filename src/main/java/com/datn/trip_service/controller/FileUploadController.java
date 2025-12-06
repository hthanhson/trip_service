package com.datn.trip_service.controller;

import com.datn.trip_service.dto.FileUploadResponse;
import com.datn.trip_service.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/image")
    public ResponseEntity<FileUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileStorageService.storeFile(file);
            String fileUrl = "/uploads/" + fileName;
            
            FileUploadResponse response = new FileUploadResponse(
                true, 
                "File uploaded successfully", 
                fileName,
                fileUrl
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            FileUploadResponse response = new FileUploadResponse(
                false, 
                "Failed to upload file: " + e.getMessage(), 
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/images")
    public ResponseEntity<List<FileUploadResponse>> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        try {
            List<FileUploadResponse> responses = new ArrayList<>();
            
            for (MultipartFile file : files) {
                try {
                    String fileName = fileStorageService.storeFile(file);
                    String fileUrl = "/uploads/" + fileName;
                    
                    responses.add(new FileUploadResponse(
                        true, 
                        "File uploaded successfully", 
                        fileName,
                        fileUrl
                    ));
                } catch (Exception e) {
                    responses.add(new FileUploadResponse(
                        false, 
                        "Failed to upload file: " + e.getMessage(), 
                        null,
                        null
                    ));
                }
            }
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/image/{fileName}")
    public ResponseEntity<FileUploadResponse> deleteImage(@PathVariable String fileName) {
        try {
            fileStorageService.deleteFile(fileName);
            FileUploadResponse response = new FileUploadResponse(
                true, 
                "File deleted successfully", 
                fileName,
                null
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            FileUploadResponse response = new FileUploadResponse(
                false, 
                "Failed to delete file: " + e.getMessage(), 
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
