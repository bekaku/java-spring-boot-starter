package com.bekaku.api.spring.util;

import com.bekaku.api.spring.configuration.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdHolder {
    private static SnowflakeIdGenerator generator;

    @Autowired
    public SnowflakeIdHolder(SnowflakeIdGenerator injectedGenerator) {
        SnowflakeIdHolder.generator = injectedGenerator;
    }

    public static SnowflakeIdGenerator generator() {
        return generator;
    }
}
