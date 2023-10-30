package com.grandats.api.givedeefive.properties;

import com.grandats.api.givedeefive.vo.UploadImageConfig;
import com.grandats.api.givedeefive.util.ConstantData;
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
    private String testProp;
    private String version;
    private MailConfig mailConfig;//app.mail-config
    private UploadImageConfig uploadImage;//app.upload-image
    private List<String> defaultRecipients;//app.default-recipients
    private Map<String, String> additionalHeaders;//app.additional-headers
    private List<MenuConfig> menus = new ArrayList<>();//app.menus
    private JwtConfig jwt;
    private QueueConfig queue;
    private String cdnPath;
    private String cdnPathAlias;
    private String url;
    private String port;
    private String cdnUrl;
    private String cdnPort;
    private List<String> allowMimes;//app.allow-mimes

    public String getUploadPath() {
        return getCdnPath().replace("file:///", "");
    }

    public String getCdnForPublic() {
        return getCdnUrl() + (getCdnPort().equalsIgnoreCase("80") || getCdnPort().equalsIgnoreCase("443") ? "" : ConstantData.COLON + getCdnPort()) + ConstantData.BACK_SLACK + getCdnPathAlias();
    }
}
