package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.properties.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FileManagerControllerTest {

    private static final String ENDPOINT = "/api/fileManager/images";
    private static final byte[] IMAGE = {(byte) 0xff, (byte) 0xd8, (byte) 0xff, (byte) 0xd9};

    @TempDir
    Path temporaryDirectory;

    @Mock
    AppProperties appProperties;

    @InjectMocks
    FileManagerController controller;

    private Path uploadRoot;
    private MockMvc mvc;

    @BeforeEach
    void setUp() throws Exception {
        uploadRoot = Files.createDirectory(temporaryDirectory.resolve("uploads"));
        when(appProperties.getUploadPath()).thenReturn(uploadRoot.toString());
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void servesNestedImage() throws Exception {
        Path images = Files.createDirectory(uploadRoot.resolve("images"));
        Files.write(images.resolve("photo.jpg"), IMAGE);

        mvc.perform(get(ENDPOINT).param("path", "images/photo.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(IMAGE));
    }

    @Test
    void missingFileReturnsNotFound() throws Exception {
        mvc.perform(get(ENDPOINT).param("path", "missing.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void directoryIsNotServed() throws Exception {
        Files.createDirectory(uploadRoot.resolve("images"));

        mvc.perform(get(ENDPOINT).param("path", "images"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsParentTraversal() throws Exception {
        Files.write(temporaryDirectory.resolve("outside.jpg"), IMAGE);

        mvc.perform(get(ENDPOINT).param("path", "../outside.jpg"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""));
    }

    @Test
    void rejectsSiblingWithSameDirectoryPrefix() throws Exception {
        Path sibling = Files.createDirectory(temporaryDirectory.resolve("uploads-backup"));
        Files.write(sibling.resolve("outside.jpg"), IMAGE);

        mvc.perform(get(ENDPOINT).param("path", "../uploads-backup/outside.jpg"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsAbsolutePathOutsideRoot() throws Exception {
        Path outside = Files.write(temporaryDirectory.resolve("outside.jpg"), IMAGE);

        mvc.perform(get(ENDPOINT).param("path", outside.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsAbsolutePathEvenInsideRoot() throws Exception {
        Path inside = Files.write(uploadRoot.resolve("photo.jpg"), IMAGE);

        mvc.perform(get(ENDPOINT).param("path", inside.toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsUrlEncodedTraversal() throws Exception {
        Files.write(temporaryDirectory.resolve("outside.jpg"), IMAGE);

        mvc.perform(get(URI.create(ENDPOINT + "?path=%2e%2e%2foutside.jpg")))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsFileSymlinkOutsideRoot() throws Exception {
        Path outside = Files.write(temporaryDirectory.resolve("outside.jpg"), IMAGE);
        Files.createSymbolicLink(uploadRoot.resolve("photo.jpg"), outside);

        mvc.perform(get(ENDPOINT).param("path", "photo.jpg"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsDirectorySymlinkOutsideRoot() throws Exception {
        Path outside = Files.createDirectory(temporaryDirectory.resolve("private"));
        Files.write(outside.resolve("photo.jpg"), IMAGE);
        Files.createSymbolicLink(uploadRoot.resolve("images"), outside);

        mvc.perform(get(ENDPOINT).param("path", "images/photo.jpg"))
                .andExpect(status().isForbidden());
    }

    @Test
    void allowsSymlinkWhoseTargetRemainsInsideRoot() throws Exception {
        Path inside = Files.write(uploadRoot.resolve("photo.jpg"), IMAGE);
        Files.createSymbolicLink(uploadRoot.resolve("alias.jpg"), inside);

        mvc.perform(get(ENDPOINT).param("path", "alias.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(IMAGE));
    }

    @Test
    void allowsConfiguredRootThatIsASymlink() throws Exception {
        Files.write(uploadRoot.resolve("photo.jpg"), IMAGE);
        Path rootAlias = Files.createSymbolicLink(temporaryDirectory.resolve("storage"), uploadRoot);
        when(appProperties.getUploadPath()).thenReturn(rootAlias.toString());

        mvc.perform(get(ENDPOINT).param("path", "photo.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(IMAGE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid\u0000.jpg"})
    void malformedPathReturnsBadRequest(String path) throws Exception {
        mvc.perform(get(ENDPOINT).param("path", path))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refusesEmptyStorageConfiguration() throws Exception {
        when(appProperties.getUploadPath()).thenReturn("");

        mvc.perform(get(ENDPOINT).param("path", "photo.jpg"))
                .andExpect(status().isInternalServerError());
    }
}
