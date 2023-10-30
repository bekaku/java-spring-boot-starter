package com.grandats.api.givedeefive.queue;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Deprecated
//@Configuration
public class SenderConfig {
    @Value("${app.queue.calculateScoreLogsName}")
    private String calculateScoreLogsName;

    @Value("${app.queue.sendNotifyName}")
    private String sendNotifyName;

    private static final boolean NON_DURABLE = false;
    private static final boolean DURABLE = true;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(calculateScoreLogsName);
    }

    @Bean
    public QueueSender sender() {
        return new QueueSender();
    }


    @Bean
    public Queue calculateScoreQueue() {
        return new Queue(calculateScoreLogsName, DURABLE);
    }

    @Bean
    public Queue sendNotifyQueue() {
        return new Queue(sendNotifyName, NON_DURABLE);
    }
}
