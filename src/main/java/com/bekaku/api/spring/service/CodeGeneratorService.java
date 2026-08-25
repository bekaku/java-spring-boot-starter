package com.bekaku.api.spring.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGeneratorService {

    private final Configuration freemarkerConfig;
    public void generateFile(String templateName, Map<String, Object> dataModel, String outputPath) {
        try {
            File outputFile = new File(outputPath);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            Template template = freemarkerConfig.getTemplate(templateName);

            try (Writer writer = new FileWriter(outputFile, false)) {
                template.process(dataModel, writer);
            }

            log.info("Successfully generated file: {}", outputPath);

        } catch (Exception e) {
            log.error("Failed to generate code from template: {} to path: {}", templateName, outputPath, e);
            throw new RuntimeException("Code generation failed", e);
        }
    }
}
