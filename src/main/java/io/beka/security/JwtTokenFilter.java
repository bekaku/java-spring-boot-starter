package io.beka.security;


import io.beka.model.data.UserData;
import io.beka.model.data.UserWithToken;
import io.beka.model.dto.AuthenticationResponse;
import io.beka.model.entity.Role;
import io.beka.model.entity.User;
import io.beka.repository.AccessTokenRepository;
import io.beka.repository.UserRepository;
import io.beka.service.JwtService;
import io.beka.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = "Authorization";
        getTokenString(request.getHeader(header)).flatMap(token -> jwtService.getSubFromToken(token)).ifPresent(refreshToken -> {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                accessTokenRepository.findByToken(refreshToken).ifPresent(accessToken -> {
//                    userRepository.findByEmail(email).ifPresent(user -> {
                    User user = accessToken.getUser();
                    if (user.getStatus()) {
                        UserData userData = new UserData();
                        userData.setId(user.getId());
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
}
