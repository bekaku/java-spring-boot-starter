package com.bekaku.api.spring.configuration;


import com.bekaku.api.spring.dto.UserDto;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class JwtTokenFilter extends OncePerRequestFilter {


    @Autowired
    private JwtService jwtService;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> SKIP_PATHS = List.of(
            "/api/public/**",
            "/api/auth/**",
            "/schedule/**",
            "/cdn/**",
            "/favicon.ico",
            "/_websocket/**",
            "/dev/development/**",
            "/test/**",
            "/welcome",
            "/theymeleaf"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        logger.info("JwtTokenFilter > doFilterInternal");
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<UserDto> userData = jwtService.jwtVerify(request.getHeader(ConstantData.ACCEPT_APIC_LIENT), request.getHeader(ConstantData.AUTHORIZATION), request.getHeader(ConstantData.X_SYNC_ACTIVE));
//            logger.info("JwtVerify User data : {}", userData.<Object>map(UserDto::getEmail).orElse(null));
            if (userData.isPresent()) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userData.get(),
                        null,
                        Collections.emptyList()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid or missing token\"}");
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SKIP_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path)) ||
                "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}

