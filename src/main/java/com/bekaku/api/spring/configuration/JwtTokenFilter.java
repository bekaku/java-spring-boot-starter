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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class JwtTokenFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private JwtService jwtService;

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
            }
        }
        filterChain.doFilter(request, response);
    }
}

