package com.grandats.api.givedeefive.properties;

import lombok.Data;

@Data
public class JwtConfig {
    private String secret;
    private int sessionTime;
}
