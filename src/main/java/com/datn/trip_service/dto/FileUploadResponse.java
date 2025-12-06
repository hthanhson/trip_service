package com.datn.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private Boolean success;
    private String message;
    private String fileName;
    private String fileUrl;
}
