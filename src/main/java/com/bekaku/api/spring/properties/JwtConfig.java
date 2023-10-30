package com.bekaku.api.spring.properties;

import lombok.Data;

@Data
public class JwtConfig {
    private String secret;
    private int sessionTime;
}
