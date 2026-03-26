package com.bekaku.api.spring.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration(proxyBeanMethods = false)
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

    @Bean
    public Queue appQueueScheduleCalculateDe() {
        return new Queue(QUEUE_SHEDULE_CALCULATE_DE, NON_DURABLE);
    }

    @Bean
    public Queue appQueueScheduleCalculateUserLevel() {
        return new Queue(QUEUE_SHEDULE_CALCULATE_USER_LEVEL, NON_DURABLE);
    }

    @Bean
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
    public Declarables bindings(
            TopicExchange exchange,
            Queue appQueueGeneric,
            Queue appQueueCalculateScore,
            Queue appQueueSendNotify,
            Queue appQueueScheduleCalculateDe,
            Queue appQueueScheduleCalculateUserLevel,
            Queue appQueueRewardTradeProcess
    ) {
        return new Declarables(
                BindingBuilder.bind(appQueueGeneric).to(exchange).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueCalculateScore).to(exchange).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueSendNotify).to(exchange).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueScheduleCalculateDe).to(exchange).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueScheduleCalculateUserLevel).to(exchange).with(ROUTING_KEY),
                BindingBuilder.bind(appQueueRewardTradeProcess).to(exchange).with(ROUTING_KEY)
        );
    }

    // You can comment the two methods below to use the default serialization / deserialization (instead of JSON)
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public JacksonJsonMessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}