package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.ai.AiFaceRegconitionServiceClient;
import com.bekaku.api.spring.dto.AiFaceResponse;
import com.bekaku.api.spring.dto.FaceRecognitionDtos;
import com.bekaku.api.spring.dto.ImageDto;
import com.bekaku.api.spring.exception.BaseResponseException;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.AppUserFace;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AppUserFaceService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.FaceRecognitionService;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaceRecognitionServiceImpl extends BaseResponseException implements FaceRecognitionService {

    private final AppProperties appProperties;
    private final AiFaceRegconitionServiceClient aiFaceRegconitionServiceClient;
    private final AppUserFaceService appUserFaceService;
    private final AppUserService appUserService;
    private final FileManagerService fileManagerService;

    @Transactional
    @Override
    public FaceRecognitionDtos.RegisterResponse registerhFace(FaceRecognitionDtos.RegisterRequest request) {

        Optional<AppUserFace> existFace = appUserFaceService.findByAppUserId(request.userId());
        if (existFace.isPresent()) {
            //remove old face
            FileManager oldFile = existFace.get().getFileManager();
            appUserFaceService.delete(existFace.get());
            if(oldFile!=null){
                fileManagerService.deleteFileBy(oldFile);
            }
        }

        Optional<FileManager> file = fileManagerService.findById(request.fileManagerId());
        if (file.isEmpty()) {
            throw this.responseError(HttpStatus.NOT_FOUND, "File not found: " + request.fileManagerId());
        }

        String filePath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), file.get().getFilePath(), false);

        Optional<AppUser> appUser = appUserService.findById(request.userId());
        if (appUser.isEmpty()) {
            throw this.responseError(HttpStatus.NOT_FOUND, "User not found: " + request.userId());
        }

        AiFaceResponse aiResponse = aiFaceRegconitionServiceClient.extractFaceFeatures(filePath);

        if (!aiResponse.success() || aiResponse.faceCount() == 0) {
            throw this.responseError(HttpStatus.BAD_REQUEST, "No face detected in the registration image.");
        }

        if (aiResponse.faceCount() > 1) {
            throw this.responseError(HttpStatus.BAD_REQUEST, "Multiple faces detected. Please provide an image with only one person.");
        }

        AiFaceResponse.FaceDetail face = aiResponse.faces().getFirst();
        if (face.detScore() < appProperties.faceRecognition().minDetectionScore()) {
            throw this.responseError(HttpStatus.BAD_REQUEST, "Image quality is too low. Confidence: " + face.detScore());
        }

        // Convert Vector List<Double> -> "[0.123, -0.456, ...]" for pgvector
        String vectorString = formatVectorToString(face.embedding());
        AppUserFace appUserFace = new AppUserFace();
        appUserFace.setEmbedding(vectorString);
        appUserFace.setAppUser(appUser.get());
        appUserFace.setFileManager(file.get());
        appUserFaceService.save(appUserFace);
        log.info("Successfully registered face for user: {}", appUser.get().getId());

        return new FaceRecognitionDtos.RegisterResponse(
                appUserFace.getId(),
                appUser.get().getId(),
                appUserFace.getCreatedDate()
        );

    }

    @Transactional
    @Override
    public FaceRecognitionDtos.DetechResponse verifyFace(MultipartFile image, String deviceId) {

        AiFaceResponse aiResponse = aiFaceRegconitionServiceClient.extractFaceFeatures(image);

        log.info("aiResponse :faceCount {},", aiResponse.faceCount());
        if (!aiResponse.success() || aiResponse.faceCount() == 0) {
            throw this.responseError(HttpStatus.BAD_REQUEST, "No face detected in the frame.");
        }

// Select the face you are most confident with (Highest Detection Score).
        AiFaceResponse.FaceDetail bestFace = aiResponse.faces().stream()
                .max((f1, f2) -> Double.compare(f1.detScore(), f2.detScore()))
                .orElseThrow();
        log.info("FaceDetail :bbox {}, detScore:{}, isRealFace:{}, livenessScore:{}", bestFace.bbox(), bestFace.detScore(), bestFace.isRealFace(), bestFace.livenessScore());

        if (bestFace.isRealFace() == null) {
            // ป้องกันมนุษย์: anti-spoof model ไม่ได้โหลดฝั่ง Python (เช็ค /health)
            // fail-closed เพราะนี่คือ security gate ห้ามปล่อยผ่านเงียบๆ
            log.error("Liveness check returned null — antispoof model likely not loaded on face-detection service");
            throw this.responseError(HttpStatus.SERVICE_UNAVAILABLE, "Liveness check unavailable, please try again later.");
        }

        if (!bestFace.isRealFace()) {
            throw this.responseError(HttpStatus.BAD_REQUEST, "A photo or video has been detected on the screen. Please use your real face.");
        }

        if (bestFace.detScore() < appProperties.faceRecognition().minDetectionScore()) {
            throw this.responseError(HttpStatus.BAD_REQUEST, "Face detection confidence too low.");
        }

        double matchThreshold = appProperties.faceRecognition().matchThreshold();

        String targetVector = formatVectorToString(bestFace.embedding());
        // Search the database using Cosine Distance
        FaceRecognitionDtos.FaceMatchProjection match = appUserFaceService.findClosestFace(targetVector, matchThreshold);

        if (match == null) {
            log.warn("Face not recognized. Minimum threshold {} was not satisfied.", matchThreshold);
            throw this.responseError(HttpStatus.NOT_FOUND, "Face not recognized. Access denied.");
        }

        //Calculate the percentage of similarity (Similarity %).
        double similarityScore = Math.max(0.0, (1.0 - match.distance()) * 100.0);

        log.info("Detection success: {} ({}) Distance: {}", match.appUserId(), match.username(), match.distance());
        ImageDto imageDto = fileManagerService.getImageDtoBy(match.fileMimeName(), match.filePath(), false);

        return new FaceRecognitionDtos.DetechResponse(
                match.appUserId(),
                match.email(),
                match.username(),
                Math.round(similarityScore * 100.0) / 100.0,
                match.distance(),
                imageDto,
                DateUtil.getLocalDateTimeNow(),
                "SUCCESS"
        );

    }

    /**
     * The PostgreSQL pgvector function to convert List<Double> to String format is supported by: [0.12, -0.45, ...]
     */
    private String formatVectorToString(List<Double> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.size(); i++) {
            sb.append(vector.get(i));
            if (i < vector.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
