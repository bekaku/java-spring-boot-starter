package com.grandats.api.givedeefive.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

//@Component
public class CronScheduler {
    private final Logger logger = LoggerFactory.getLogger(CronScheduler.class);

//    @Scheduled(cron = "${app.cron.test-expression}")
    public void cronScheduleTask() {
        logger.info("schedule tasks using cronScheduleTask - {}", ZonedDateTime.now());
    }
}
