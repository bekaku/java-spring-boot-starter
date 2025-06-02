package com.bekaku.api.spring.logger;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Locale;

@Slf4j
@Component
public class PerformanceMonitor {

    @Autowired
    private DataSource dataSource;
    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        Runtime runtime = Runtime.getRuntime();
        long startTime = System.currentTimeMillis();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        log.info("Memory usage: {}MB", usedMemory / 1024 / 1024);
        log.info("Startup time: {}ms",
                System.currentTimeMillis() - startTime);
    }

    @PostConstruct
    private void checkDataSourceType() {
        log.info("DataSource class: {}", dataSource.getClass().getName());
        if (dataSource instanceof HikariDataSource) {
            log.info("HikariCP is enabled.");
        }
    }
}
