package com.bekaku.api.spring.controller.api;


import com.bekaku.api.spring.dto.FaceRecognitionDtos;
import com.bekaku.api.spring.service.AppUserFaceService;
import com.bekaku.api.spring.service.AppUserService;
import com.bekaku.api.spring.service.FaceRecognitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping(path = "/api/faceRegconition")
@RestController
@RequiredArgsConstructor
public class FaceRegconitionController extends BaseApiController {

    private final FaceRecognitionService faceRecognitionService;
    private final AppUserService appUserService;
    private final AppUserFaceService appUserFaceService;

    @PostMapping(value = "/register")
    public ResponseEntity<FaceRecognitionDtos.RegisterResponse> register(@Valid @RequestBody FaceRecognitionDtos.RegisterRequest request
    ) {
        FaceRecognitionDtos.RegisterResponse response = faceRecognitionService.registerhFace(request);
        return this.responseEntity(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/detection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FaceRecognitionDtos.DetechResponse> checkIn(
            @RequestPart("image") MultipartFile image,
            @RequestParam(value = "deviceId", required = false) String deviceId
    ) {
        FaceRecognitionDtos.DetechResponse response = faceRecognitionService.verifyFace(image, deviceId);
        return ResponseEntity.ok(response);
    }
}
