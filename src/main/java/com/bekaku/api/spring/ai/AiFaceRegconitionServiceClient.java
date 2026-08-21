package com.bekaku.api.spring.ai;

import com.bekaku.api.spring.dto.AiFaceResponse;
import com.bekaku.api.spring.properties.AppProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Component
public class AiFaceRegconitionServiceClient {

    private final RestClient restClient;

    public AiFaceRegconitionServiceClient(AppProperties appProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = RestClient.builder()
                .baseUrl(appProperties.faceRecognition().baseUrl())
                .requestFactory(factory)
                .build();
    }
    public AiFaceResponse extractFaceFeatures(String filePath) {
        try {
            Path path = Paths.get(filePath);

            // Verify that the file actually exists on the server.
            if (!Files.exists(path)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image file not found on server at: " + filePath);
            }

            // Use FileSystemResource to read the file from Disk and attach it as multipart/form-data.
            FileSystemResource resource = new FileSystemResource(path);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            return restClient.post()
                    .uri("/extract-face")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(AiFaceResponse.class);

        } catch (ResponseStatusException e) {
            throw e; // Throw out the HTTP status we intercepted.
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI Extraction Service unavailable: " + e.getMessage());
        }
    }
    public AiFaceResponse extractFaceFeatures(MultipartFile file) {
        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            return restClient.post()
                    .uri("/extract-face")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(AiFaceResponse.class);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to read image file: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI Extraction Service unavailable: " + e.getMessage());
        }
    }
}
