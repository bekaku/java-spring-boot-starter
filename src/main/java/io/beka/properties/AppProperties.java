package io.beka.properties;

import io.beka.vo.MailConfig;
import io.beka.vo.MenuConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String version;
    private MailConfig mailConfig;//app.mail-config
    private List<String> defaultRecipients;//app.default-recipients
    private Map<String, String> additionalHeaders;//app.additional-headers
    private List<MenuConfig> menus = new ArrayList<>();//app.menus
}
