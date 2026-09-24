package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.util.FileUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises the real public CDN resource handler from {@link WebConfigurerAdapter} against a
 * {@code @TempDir} storage root. Resource handlers cannot be registered on standalone MockMvc, so this
 * uses a minimal MVC context; the security chain is not involved ({@code /cdn/**} is permitAll there).
 */
class WebConfigurerAdapterCdnTest {

    private static final String LOG_LINE = "INFO uid=42 ip=10.0.0.1 login";
    private static final String CHUNK = "upload-7f3a.pdf.part1";

    @TempDir
    Path cdnRoot;

    private AnnotationConfigWebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(cdnRoot.resolve("images/202609"));
        Files.writeString(cdnRoot.resolve("images/202609/avatar.png"), "public-image");
        Files.createDirectories(cdnRoot.resolve("images/logs"));
        Files.writeString(cdnRoot.resolve("images/logs/nested.log"), LOG_LINE);
        Files.createDirectories(cdnRoot.resolve("logs/2026-09"));
        Files.writeString(cdnRoot.resolve("logs/spring-boot-logger-log4j2.log"), LOG_LINE);
        Files.writeString(cdnRoot.resolve("logs/2026-09/spring-boot-logger-log4j2-25-September-2026-1.log.gz"), LOG_LINE);
        Files.createDirectories(cdnRoot.resolve("env"));
        Files.writeString(cdnRoot.resolve("env/application.yml"), "spring.datasource.password: not-a-real-secret");
        Files.createDirectories(cdnRoot.resolve(FileUtil.TEMP_UPLOAD_DIR));
        Files.writeString(cdnRoot.resolve(FileUtil.TEMP_UPLOAD_DIR + CHUNK), "partial-upload");

        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("cdn", Map.of(
                "app.cdn-path", cdnRoot.toUri().toString(),
                "app.cdn-path-alias", "cdn")));
        context.register(CdnMvcConfig.class);
        context.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    @Test
    void servesPublicUpload() throws Exception {
        mvc.perform(get("/cdn/images/202609/avatar.png"))
                .andExpect(status().isOk())
                .andExpect(content().string("public-image"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/cdn/logs/spring-boot-logger-log4j2.log",
            "/cdn/logs/2026-09/spring-boot-logger-log4j2-25-September-2026-1.log.gz",
            "/cdn/images/logs/nested.log",
            "/cdn/%6Cogs/spring-boot-logger-log4j2.log",
            "/cdn/LOGS/spring-boot-logger-log4j2.log",
            "/cdn/env/application.yml",
            "/cdn/%65nv/application.yml",
            "/cdn/" + FileUtil.TEMP_UPLOAD_DIR + CHUNK
    })
    void doesNotServePrivateFolders(String path) throws Exception {
        mvc.perform(get(URI.create(path)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "logs/app.log",
            "LoGs/app.log",
            "images/202609/logs/app.log",
            "%6Cogs/app.log",
            "images%2Flogs%2Fapp.log",
            "images%5Clogs%5Capp.log",
            "logs./app.log",                   // Windows drops trailing dots/spaces
            "logs%20/app.log",
            "logs::$INDEX_ALLOCATION/app.log", // NTFS directory stream
            "logs;v=1/app.log",
            "images/%zz/app.log"               // malformed escape
    })
    void guardRefusesLogsFolderVariants(String path) {
        ResourceResolverChain chain = mock(ResourceResolverChain.class);
        CdnDeniedFolderResourceResolver resolver = new CdnDeniedFolderResourceResolver("logs");

        assertThat(resolver.resolveResource(null, path, List.of(), chain)).isNull();
        assertThat(resolver.resolveUrlPath(path, List.of(), chain)).isNull();
        verifyNoInteractions(chain);
    }

    @ParameterizedTest
    @ValueSource(strings = {"images/202609/avatar.png", "files/202609/logsheet.pdf", "medias/202609/logs.mp4"})
    void guardDelegatesOtherPaths(String path) {
        ResourceResolverChain chain = mock(ResourceResolverChain.class);
        Resource resource = new ByteArrayResource(new byte[0]);
        when(chain.resolveResource(null, path, List.of())).thenReturn(resource);

        assertThat(new CdnDeniedFolderResourceResolver("logs").resolveResource(null, path, List.of(), chain))
                .isSameAs(resource);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    @Import(WebConfigurerAdapter.class)
    static class CdnMvcConfig {
    }
}
