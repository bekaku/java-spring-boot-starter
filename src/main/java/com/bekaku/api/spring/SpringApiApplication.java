package com.bekaku.api.spring;

import com.bekaku.api.spring.configuration.AppHints;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

//@EnableRabbit
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.bekaku.api")
@EnableJpaAuditing
//@EnableScheduling
@ConfigurationPropertiesScan
@ImportRuntimeHints(AppHints.class)
@EnableAsync
public class SpringApiApplication {

    @Value("${environments.production}")
    boolean isProduction;

    public static void main(String[] args) {
        SpringApplication.run(SpringApiApplication.class, args);
    }

}
