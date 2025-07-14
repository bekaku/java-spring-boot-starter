package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.CorsProperties;
import com.bekaku.api.spring.util.ConstantData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    @Autowired
    private CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders(
                        ConstantData.CACHE_CONTROL,
                        ConstantData.CONTENT_TYPE,
                        ConstantData.AUTHORIZATION,
                        ConstantData.ACCEPT_LANGUGE,
                        ConstantData.ACCEPT_APIC_LIENT,
                        ConstantData.X_SYNC_ACTIVE,
                        ConstantData.X_USER_ID
                )
                .exposedHeaders(ConstantData.CONTENT_DISPOSITION, "Set-Cookie")
                .allowCredentials(true)
                .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .maxAge(3600);
//                .allowedOriginPatterns("*");
//                .allowedOrigins(
//                        "http://localhost:8080",    // Dev
//                        "capacitor://localhost",    // Capacitor IOS
//                        "http://localhost", // Capacitor Android
//                        "https://192.168.1.100"      // local IP บน LAN
//                );


        // onother mapping for /public/** have diference allowedOrigins
        /*
        registry.addMapping("/public/**")
                .allowedOrigins("https://another-public-domain.com", "http://another-dev-domain.com")
                .maxAge(3600);
         */
    }
}
