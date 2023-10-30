package com.grandats.api.givedeefive;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grandats.api.givedeefive.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableRabbit
@SpringBootApplication
@EnableJpaAuditing
//@EnableScheduling
@MapperScan("com.grandats.api.givedeefive.mapper") // mybatis scan path for interface Mapper class
public class GivedeefiveApplication {

    @Value("${environments.production}")
    boolean isProduction;

    public static void main(String[] args) {
        SpringApplication.run(GivedeefiveApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
