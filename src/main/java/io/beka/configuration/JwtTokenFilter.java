package io.beka.configuration;


import io.beka.dto.UserData;
import io.beka.model.ApiClient;
import io.beka.model.User;
import io.beka.repository.AccessTokenRepository;
import io.beka.repository.ApiClientRepository;
import io.beka.repository.UserRepository;
import io.beka.service.JwtService;
import io.beka.util.ConstantData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class JwtTokenFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApiClientRepository apiClientRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("JwtTokenFilter > doFilterInternal");
        verifyApiClient(request.getHeader(ConstantData.ACCEPT_APIC_LIENT)).flatMap(apiClient ->
                getTokenString(request.getHeader(ConstantData.AUTHORIZATION)).flatMap(token ->
                        jwtService.getSubFromToken(token, apiClient))).ifPresent(refreshToken -> {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                accessTokenRepository.findByToken(refreshToken, false).ifPresent(accessToken -> {
                    User user = accessToken.getUser();
                    if (user.getStatus()) {
                        UserData userData = new UserData();
                        userData.setId(user.getId());
                        userData.setToken(refreshToken);
                        userData.setUsername(user.getUsername());
                        userData.setEmail(user.getEmail());
                        userData.setImage(user.getImage());
                        userData.setStatus(user.getStatus());
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userData,
                                null,
                                Collections.emptyList()
                        );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                });
            }
        });
        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenString(String header) {
        if (header == null) {
            return Optional.empty();
        } else {
            String[] split = header.split(" ");
            if (split.length < 2) {
                return Optional.empty();
            } else {
                return Optional.ofNullable(split[1]);
            }
        }
    }

    private Optional<ApiClient> verifyApiClient(String apiName) {
        if (apiName != null) {
            Optional<ApiClient> apiClient = apiClientRepository.findByApiName(apiName);
            if (apiClient.isPresent()) {
                return apiClient.get().getStatus() || apiClient.get().getByPass() ? apiClient : Optional.empty();
            }
        }
        return Optional.empty();
    }
}

