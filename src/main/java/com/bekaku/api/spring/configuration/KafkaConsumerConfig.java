//package com.bekaku.api.spring.configuration;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Bean(name = "group1ConsumerFactory")
//    public ConsumerFactory<String, Object> group1ConsumerFactory(KafkaProperties kafkaProperties) {
//        return new DefaultKafkaConsumerFactory<>(
//                kafkaProperties.buildConsumerProperties(),
//                new org.apache.kafka.common.serialization.StringDeserializer(),
//                new org.springframework.kafka.support.serializer.JsonDeserializer<>(Object.class)
//        );
//    }
//
//    @Bean(name = "group1KafkaListenerContainerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, Object> group1KafkaListenerContainerFactory(
//            @Qualifier("group1ConsumerFactory") ConsumerFactory<String, Object> consumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        return factory;
//    }
//
//    /*
//    //additional  Consumer Factory for group2
//    @Bean(name = "group2ConsumerFactory")
//    public ConsumerFactory<String, User> group2ConsumerFactory(KafkaProperties kafkaProperties) {
//        return new DefaultKafkaConsumerFactory<>(
//                kafkaProperties.buildConsumerProperties(),
//                new org.apache.kafka.common.serialization.StringDeserializer(),
//                new org.springframework.kafka.support.serializer.JsonDeserializer<>(User.class)
//        );
//    }
//
//    //additional Listener Container Factory for group2
//    @Bean(name = "group2KafkaListenerContainerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, User> group2KafkaListenerContainerFactory(
//            @Qualifier("group2ConsumerFactory") ConsumerFactory<String, User> consumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<String, User> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        return factory;
//    }
//
//     */
//}
