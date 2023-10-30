package com.bekaku.api.spring.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class QueueConfig {
    public static final String EXCHANGE_NAME = "com.bekaku.api.spring";
    public static final String QUEUE_GENERIC_NAME = "com.bekaku.api.spring.generic";
    public static final String QUEUE_CALCULATE_SCORE = "com.bekaku.api.spring.calculate.score";
    public static final String QUEUE_SEND_NOTIFY = "com.bekaku.api.spring.send.notify";
    public static final String QUEUE_SHEDULE_CALCULATE_DE = "com.bekaku.api.spring.schedule.calculate.de";
    public static final String QUEUE_SHEDULE_CALCULATE_USER_LEVEL = "com.bekaku.api.spring.schedule.calculate.user.level";
    public static final String QUEUE_REWARD_TRADE_PROCESS = "com.bekaku.api.spring.reward.trade.process";
    public static final String ROUTING_KEY = "com.bekaku.api.spring.routing.key";
    private static final boolean NON_DURABLE = false;

    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue appQueueGeneric() {
        return new Queue(QUEUE_GENERIC_NAME, NON_DURABLE);
    }

    @Bean
    public Queue appQueueCalculateScore() {
        return new Queue(QUEUE_CALCULATE_SCORE, NON_DURABLE);
    }

    @Bean
    public Queue appQueueSendNotify() {
        return new Queue(QUEUE_SEND_NOTIFY, NON_DURABLE);
    }

    public Queue appQueueScheduleCalculateDe() {
        return new Queue(QUEUE_SHEDULE_CALCULATE_DE, NON_DURABLE);
    }

    public Queue appQueueScheduleCalculateUserLevel() {
        return new Queue(QUEUE_SHEDULE_CALCULATE_USER_LEVEL, NON_DURABLE);
    }

    public Queue appQueueRewardTradeProcess() {
        return new Queue(QUEUE_REWARD_TRADE_PROCESS, NON_DURABLE);
    }

//    @Bean
//    public Binding declareBindingGeneric() {
//        return BindingBuilder.bind(appQueueGeneric()).to(appExchange()).with(ROUTING_KEY);
//    }
//
//    @Bean
//    public Binding declareBindingSpecific() {
//        return BindingBuilder.bind(appQueueCalculateScore()).to(appExchange()).with(ROUTING_KEY);
//    }
//
//    @Bean
//    public Binding declareBindingSendNotifySpecific() {
//        return BindingBuilder.bind(appQueueSendNotify()).to(appExchange()).with(ROUTING_KEY);
//    }

    @Bean
    List<Binding> bindings() {
        return Arrays.asList(
                BindingBuilder.bind(appQueueGeneric()).to(appExchange()).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueCalculateScore()).to(appExchange()).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueSendNotify()).to(appExchange()).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueScheduleCalculateDe()).to(appExchange()).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueScheduleCalculateUserLevel()).to(appExchange()).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueRewardTradeProcess()).to(appExchange()).with(ROUTING_KEY)
        );
    }

    // You can comment the two methods below to use the default serialization / deserialization (instead of JSON)
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
