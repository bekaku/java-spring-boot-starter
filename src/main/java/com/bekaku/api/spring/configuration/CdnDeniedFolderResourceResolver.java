package com.bekaku.api.spring.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * Defense in depth for the public CDN resource handler: refuses to resolve any path that contains a denied
 * folder name (e.g. {@code logs}), so private material that ends up under {@code app.cdn-directory} is
 * answered like a missing file instead of being served.
 * <p>
 * Segments are compared after one percent-decode (the filesystem layer decodes {@code file:} locations once),
 * case-insensitively, and without the suffixes Windows ignores (trailing dots/spaces, {@code :stream}).
 */
final class CdnDeniedFolderResourceResolver extends AbstractResourceResolver {

    private final Set<String> deniedFolders;

    CdnDeniedFolderResourceResolver(String... deniedFolders) {
        this.deniedFolders = Set.of(deniedFolders);
    }

    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
                                               List<? extends Resource> locations, ResourceResolverChain chain) {
        return isDenied(requestPath) ? null : chain.resolveResource(request, requestPath, locations);
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations,
                                            ResourceResolverChain chain) {
        return isDenied(resourceUrlPath) ? null : chain.resolveUrlPath(resourceUrlPath, locations);
    }

    private boolean isDenied(String path) {
        String decoded;
        try {
            decoded = StringUtils.uriDecode(path, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return true; // malformed escapes never belong to a server-generated upload path
        }
        for (String segment : StringUtils.tokenizeToStringArray(decoded.replace('\\', '/'), "/")) {
            String name = folderName(segment);
            if (deniedFolders.stream().anyMatch(name::equalsIgnoreCase)) {
                return true;
            }
        }
        return false;
    }

    // Windows resolves "logs.", "logs " and "logs::$INDEX_ALLOCATION" to "logs"; ";" starts a path parameter.
    private static String folderName(String segment) {
        int end = 0;
        while (end < segment.length() && segment.charAt(end) != ';' && segment.charAt(end) != ':') {
            end++;
        }
        while (end > 0 && (segment.charAt(end - 1) == '.' || segment.charAt(end - 1) == ' ')) {
            end--;
        }
        return segment.substring(0, end);
    }
}
