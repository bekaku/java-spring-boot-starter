package com.bekaku.api.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

//@EnableRabbit
@SpringBootApplication
@EnableJpaAuditing
//@EnableScheduling
@EnableAsync
@MapperScan("com.bekaku.api.spring.mybatis") // mybatis scan path for interface Mapper class
public class SpringApiApplication {

    @Value("${environments.production}")
    boolean isProduction;

    public static void main(String[] args) {
        SpringApplication.run(SpringApiApplication.class, args);
    }

}
