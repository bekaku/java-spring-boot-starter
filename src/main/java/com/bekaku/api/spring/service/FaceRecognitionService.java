package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.FaceRecognitionDtos;
import org.springframework.web.multipart.MultipartFile;

public interface FaceRecognitionService {
    FaceRecognitionDtos.RegisterResponse registerhFace(FaceRecognitionDtos.RegisterRequest request);
    FaceRecognitionDtos.DetechResponse verifyFace(MultipartFile image, String deviceId);
}
