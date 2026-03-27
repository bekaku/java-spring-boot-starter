package com.bekaku.api.spring.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration(proxyBeanMethods = false)
@MapperScan("com.bekaku.api.spring.mybatis")
@EnableTransactionManagement
public class MyBatisConfig {
}
