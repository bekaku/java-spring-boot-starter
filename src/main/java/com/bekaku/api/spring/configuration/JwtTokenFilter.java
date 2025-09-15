package com.bekaku.api.spring.configuration;


import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.service.JwtService;
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

@Slf4j
@SuppressWarnings("SpringJavaAutowiringInspection")
public class JwtTokenFilter extends OncePerRequestFilter {


    @Autowired
    private JwtService jwtService;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> SKIP_PATHS = List.of(
            "/api/public/**",
            "/api/auth/**",
            "/api/fileManager/files/stream",
            "/api/fileManager/video/stream",
            "/schedule/**",
            "/cdn/**",
            "/favicon.ico",
            "/_websocket/**",
            "/dev/development/**",
            "/actuator/**",
            "/test/**",
            "/welcome",
            "/theymeleaf"
    );
    private static final List<String> STREAMING_ENDPOINTS = Arrays.asList(
            "/api/fileManager/files/stream",
            "/api/fileManager/video/stream"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        logger.info("JwtTokenFilter > doFilterInternal");
        String requestURI = request.getRequestURI();
        boolean isStreamingEndpoint = STREAMING_ENDPOINTS.stream()
                .anyMatch(requestURI::contains);
        log.info("JwtTokenFilter > doFilterInternal > isStreamingEndpoint: {}", isStreamingEndpoint);
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<AppUserDto> userData = jwtService.jwtVerify(
                        request.getHeader(ConstantData.ACCEPT_APIC_LIENT),
                        request.getHeader(ConstantData.AUTHORIZATION),
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
                    if (response.isCommitted()) {
                        log.warn("Cannot send unauthorized response - response already committed for: {}",
                                request.getRequestURI());
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    try {
                        response.getWriter().write("{\"error\": \"Invalid or missing token\"}");
                        response.getWriter().flush();
                        return;
                    } catch (IOException e) {
                        if (isStreamingEndpoint) {
                            log.info("Could not write unauthorized response for streaming endpoint: {}", e.getMessage());
                        } else {
                            log.error("Error writing unauthorized response: {}", e.getMessage());
                            throw e;
                        }
                    }
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SKIP_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path)) ||
                "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}

