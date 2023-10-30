package com.bekaku.api.spring.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

//@Component
public class CronScheduler {
    private final Logger logger = LoggerFactory.getLogger(CronScheduler.class);

//    @Scheduled(cron = "${app.cron.test-expression}")
    public void cronScheduleTask() {
        logger.info("schedule tasks using cronScheduleTask - {}", ZonedDateTime.now());
    }
}
