package io.beka.vo;

import lombok.Data;

@Data
public class JwtConfig {
    private String secret;
    private int sessionTime;
}
