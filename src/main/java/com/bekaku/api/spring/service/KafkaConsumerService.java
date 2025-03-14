//package com.bekaku.api.spring.service;
//
//import com.bekaku.api.spring.dto.ResponseMessage;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class KafkaConsumerService {
//
//    @KafkaListener(topics = "response-message", groupId = "user-group", containerFactory = "group1KafkaListenerContainerFactory")
//    public void consumeResponseMessage(ResponseMessage responseMessage) {
//        log.info("Received responseMessage: {}", responseMessage);
//    }
//
//    @KafkaListener(topics = "test-thread", groupId = "user-group", containerFactory = "group1KafkaListenerContainerFactory")
//    public void consumeTestThred(String message) throws InterruptedException {
//        Thread.sleep(2000);
//        log.info("Received consumeTestThred: {}", message);
//    }
//}
