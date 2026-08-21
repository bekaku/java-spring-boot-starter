package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.FaceRecognitionDtos;
import com.bekaku.api.spring.model.AppUserFace;

import java.util.Optional;

public interface AppUserFaceService extends BaseService<AppUserFace, AppUserFace> {

    Optional<AppUserFace> findByAppUserId(Long appUserId);
    boolean existsByAppUserId(Long appUserId);
    FaceRecognitionDtos.FaceMatchProjection findClosestFace(String targetVector, double threshold);
}
