package com.bekaku.api.spring.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol;
}
