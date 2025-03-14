//package com.bekaku.api.spring.serviceImpl;
//
//import com.bekaku.api.spring.service.KafkaProducerService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class KafkaProducerServiceImpl implements KafkaProducerService {
//
//    @Value("${environments.production}")
//    private boolean production;
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Override
//    public void send(String topic, Object o) {
//        kafkaTemplate.send(topic, o)
//                .whenComplete(this::logEvent);
//    }
//
//    @Override
//    public void send(String topic, String key, Object o) {
//        kafkaTemplate.send(topic, key, o)
//                .whenComplete(this::logEvent);
//    }
//
//    private void logEvent(SendResult<String, Object> result, Throwable e) {
//        if (!production) {
//            if (e != null) {
//                log.info("Kafka send fail: {}", e.getMessage());
//            } else {
//                log.info("Kafka send success: {}", result.toString());
//            }
//        }
//    }
//}
