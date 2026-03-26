package com.bekaku.api.spring.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(
        prePostEnabled = true,
        proxyTargetClass = false
)
public class SecurityEnablerConfig {
}
