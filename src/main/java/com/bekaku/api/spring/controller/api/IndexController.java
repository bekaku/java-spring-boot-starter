package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;


@Slf4j
@RequiredArgsConstructor
@RestController
public class IndexController extends BaseApiController {

    private final AppProperties appProperties;

    @GetMapping("/")
    public ResponseEntity<Object> index() {
        return responseEntity(new HashMap<String, Object>() {{
            put(ConstantData.SERVER_STATUS, true);
            put(ConstantData.SERVER_TIMESTAMP, DateUtil.getLocalDateTimeNow());
        }}, HttpStatus.OK);
    }

    @GetMapping(value = "/robots.txt", produces = "text/plain")
    public String getRobotsTxt() {
        return """
                User-agent: *
                Disallow: /
                """;
    }


    @GetMapping("/index/files/stream")
    public ResponseEntity<StreamingResponseBody> streamFile(@RequestParam("path") String fileName,
                                                            @RequestParam(defaultValue = "8192") int chunkSize,
                                                            @AuthenticationPrincipal AppUserDto auth) {
        log.info("streamFile:{}", fileName);

        // Pre-validate authentication BEFORE any file operations
        if (auth == null) {
            log.warn("Unauthenticated request for file: {}", fileName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            // Get file from classpath or file system
            Path filePath = Paths.get(appProperties.getUploadPath(), fileName); // Adjust path as needed

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            long fileSize = Files.size(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            StreamingResponseBody streamingResponseBody = outputStream -> {
                try (FileInputStream fis = new FileInputStream(filePath.toFile());
                     BufferedInputStream bis = new BufferedInputStream(fis)) {

                    byte[] buffer = new byte[chunkSize];
                    int bytesRead;

                    while ((bytesRead = bis.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        outputStream.flush();

                        // Optional: Add delay for demo purposes
                        // Thread.sleep(100);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error streaming file", e);
                }
            };

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                    .header("X-Downloaded-By", auth.getId().toString())
                    .body(streamingResponseBody);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
