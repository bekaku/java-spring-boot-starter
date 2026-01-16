package com.bekaku.api.spring.configuration;

import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class HibernateConfig implements HibernatePropertiesCustomizer {
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.integrator_provider",
                (IntegratorProvider) () -> Collections.singletonList(MetadataExtractorIntegrator.INSTANCE));
    }
}
