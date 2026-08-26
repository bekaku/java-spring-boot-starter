package com.bekaku.api.spring.configuration;


import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.properties.JwtProperties;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.service.JwtService;
import com.bekaku.api.spring.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.bekaku.api.spring.util.ConstantData.UNDER_SCORE;

@Slf4j
@SuppressWarnings("SpringJavaAutowiringInspection")
public class JwtTokenFilter extends OncePerRequestFilter {


    @Autowired
    private JwtService jwtService;

    @Autowired
    private CookieUtil cookieUtil;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> SKIP_PATHS = List.of(
            "/api/public/**",
            "/api/auth/**",
            "/schedule/**",
//            "/api/fileManager/files/stream/**",
//            "/api/fileManager/video/stream/**",
            "/cdn/**",
            "/favicon.ico",
            "/_websocket/**",
            "/dev/development/**",
            "/actuator/**",
            "/test/**",
            "/welcome",
            "/theymeleaf",
            "/api-docs/**",
            "/swagger-ui/**"
    );
    private static final List<String> STREAMING_ENDPOINTS = Arrays.asList(
            "/api/fileManager/files/stream",
            "/api/fileManager/video/stream"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        boolean isStreamingEndpoint = STREAMING_ENDPOINTS.stream()
                .anyMatch(requestURI::contains);
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                String apiClient = request.getHeader(ConstantData.ACCEPT_APIC_LIENT);

                Optional<String> jwtToken = Optional.ofNullable(cookieUtil.getCurrentUserAccessToken(request));

                if (jwtToken.isEmpty()) {
                    jwtToken = jwtService.getSubFromAuthorizationHeader(request.getHeader(ConstantData.AUTHORIZATION), null);
                }


                Optional<String> requestUserId = Optional.ofNullable(cookieUtil.getCurrentUserID(request));
                if (requestUserId.isEmpty()) {
                    requestUserId = Optional.ofNullable(request.getHeader(ConstantData.X_USER_ID));
                }
                log.info("UID:{}, Access token present:{}", requestUserId.orElse(null), jwtToken.isPresent());

                if (jwtToken.isEmpty()) {
                    log.warn("Jwt token not found : {}", request.getRequestURI());
                    sendUnauthorizedResponse(response, "Jwt token not found", isStreamingEndpoint, request.getRequestURI());
                    return;
                }

                Optional<AppUserDto> userData = jwtService.jwtVerify(
                        apiClient,
                        jwtToken.get(),
                        request.getHeader(ConstantData.X_SYNC_ACTIVE));
//            logger.info("JwtVerify User data : {}", userData.<Object>map(UserDto::getEmail).orElse(null));
                if (userData.isPresent()) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userData.get(),
                            null,
                            Collections.emptyList()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    // Check if response is already committed (streaming has started)
                    sendUnauthorizedResponse(response, "Invalid or missing token", isStreamingEndpoint, request.getRequestURI());
                    return;
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Handle exceptions gracefully for streaming endpoints
            if (isStreamingEndpoint && response.isCommitted()) {
                log.info("Exception in JWT filter for streaming endpoint after response committed: {}",
                        e.getMessage());
                // Don't try to write to response - it's already committed
                return;
            }
            throw e;
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message, boolean isStreamingEndpoint, String requestURI) throws IOException {
        if (response.isCommitted()) {
            log.warn("Cannot send unauthorized response - response already committed for: {}", requestURI);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // ยิง 401 ออกไป
        response.setContentType("application/json");

        try {
            response.getWriter().write("{\"error\": \"" + message + "\"}");
            response.getWriter().flush();
        } catch (IOException e) {
            if (isStreamingEndpoint) {
                log.info("Could not write unauthorized response for streaming endpoint: {}", e.getMessage());
            } else {
                log.error("Error writing unauthorized response: {}", e.getMessage());
                throw e;
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SKIP_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path)) ||
                "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}

