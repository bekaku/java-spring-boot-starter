package com.bekaku.api.spring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Value("${app.cdn-path-alias}")
    String cdnPathAlias;

    @Value("${environments.production}")
    boolean isProduction;

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/lib/**",
                        "/content/**",
                        "/" + cdnPathAlias + "/**",
                        "/favicon.ico",
                        "/oauth2",
                        "/_websocket/**"
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
//                .securityMatcher("/api/**", "/schedule/**", "/test/**", "/" + cdnPathAlias + "/**")
                .securityMatcher("/**") // apply this chain to ALL requests
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(HttpMethod.OPTIONS).permitAll()
//                                .requestMatchers(HttpMethod.GET, "/css/**", "/js/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/content/**").permitAll()
                            // Allow static CDN resources
                            .requestMatchers(HttpMethod.GET, "/" + cdnPathAlias + "/**").permitAll()
                            // Public auth endpoints
                            .requestMatchers(HttpMethod.POST,
                                    "/api/auth/login",
                                    "/api/auth/logout",
                                    "/api/auth/refreshToken",
                                    "/api/auth/requestVerifyCodeToResetPwd",
                                    "/api/auth/sendVerifyCodeToResetPwd",
                                    "/api/auth/resetPassword"
                            ).permitAll()
                            // Public open APIs
                            .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                            // Public schedule routes
                            .requestMatchers(HttpMethod.GET, "/schedule/**").permitAll()
                            // API routes require authentication
                            .requestMatchers("/api/**").authenticated();
                    if (!isProduction) {
                        requests
                                .requestMatchers(HttpMethod.POST, "/dev/development/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/dev/development/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/test/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/test/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/welcome", "/theymeleaf").permitAll();
                    }
//                    requests.anyRequest().authenticated();
                    requests.anyRequest().denyAll();
                })
                .headers(headers ->
                        headers.xssProtection(
                                xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        ).contentSecurityPolicy(
                                cps -> cps.policyDirectives("script-src 'self'")
                        ))
//                .httpBasic(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
