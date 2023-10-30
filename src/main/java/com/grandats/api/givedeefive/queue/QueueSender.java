package com.grandats.api.givedeefive.queue;

import com.grandats.api.givedeefive.queue.dto.*;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
@Component
public class QueueSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /*
        @Autowired
        private TopicExchange topicExchange;

        @Value("${app.queue.key}")
        private String queueKey;

        @Autowired
        private Queue calculateScoreQueue;

        @Autowired
        private Queue sendNotifyQueue;

        public void sendTopic(String email) {
            rabbitTemplate.convertAndSend(topicExchange.getName(), queueKey, email);
        }

        //    public void calculateScore(String vo) {
    //        rabbitTemplate.convertAndSend(this.calculateScoreQueue.getName(), vo);
    //    }
        public void calculateScore(CalculateUserScoreLogsVo vo) {
    //        rabbitTemplate.convertAndSend(this.calculateScoreQueue.getName(), vo);
            final var message = new CustomMessage("Hello there!", new Random().nextInt(50), false);
            rabbitTemplate.convertAndSend(this.calculateScoreQueue.getName(), queueKey, message);
        }
*/
//    public void sendNotify() {
//        rabbitTemplate.convertAndSend(this.sendNotifyQueue.getName(), email);
//    }

    public void sendNotify(SendNotifyQueue notifyQueue) {
        rabbitTemplate.convertAndSend(QueueConfig.QUEUE_SEND_NOTIFY, notifyQueue);
    }

    public void calculateScore(CalculateScoreQueue dto) {
        rabbitTemplate.convertAndSend(QueueConfig.QUEUE_CALCULATE_SCORE, dto);
    }

    public void calculateDe(ScheduleCalculateDe dto) {
        rabbitTemplate.convertAndSend(QueueConfig.QUEUE_SHEDULE_CALCULATE_DE, dto);
    }

    public void calculateUserLevel(ScheduleCalculateUserLevel dto) {
        rabbitTemplate.convertAndSend(QueueConfig.QUEUE_SHEDULE_CALCULATE_USER_LEVEL, dto);
    }
    public void rewardTradeProcess(RewardTradeProcessDto dto) {
        rabbitTemplate.convertAndSend(QueueConfig.QUEUE_REWARD_TRADE_PROCESS, dto);
    }

    private void createQueueIfNotExist(String queueName) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(queueName, true, false, false, null);
            AMQP.Queue.DeclareOk declareOkExists = channel.queueDeclarePassive(queueName);
        }
    }
}
