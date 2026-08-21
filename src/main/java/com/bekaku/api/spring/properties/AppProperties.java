package com.bekaku.api.spring.properties;

import com.bekaku.api.spring.util.ConstantData;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String testProp,
        String version,
        MailConfig mailConfig,          // app.mail-config
        UploadImageConfig uploadImage,  // app.upload-image
        List<String> defaultRecipients, // app.default-recipients
        Map<String, String> additionalHeaders, // app.additional-headers
        List<MenuConfig> menus,         // app.menus
        JwtProperties jwt,
        CookieProperties cookie,
        QueueConfig queue,
        String cdnPath,
        String cdnPathAlias,
        String url,
        String port,
        String cdnUrl,
        String cdnPort,
        List<String> allowMimes,         // app.allow-mimes
        AppCronProperties cron,
        RagProperties rag,
        FaceRecognitionProperties faceRecognition

) {

    // How to handle Default Value (replace = new ArrayList<>())
    // We use the Compact Constructor to check if Spring hasn't bound the values, and if so, set the list to empty.
    public AppProperties {
        if (menus == null) {
            menus = List.of(); // or new java.util.ArrayList<>()
        }
        if (defaultRecipients == null) {
            defaultRecipients = List.of();
        }
        if (allowMimes == null) {
            allowMimes = List.of();
        }
    }

    // Custom methods can be written directly inside the body.
    // Notice that we've stopped using getCdnPath() and can now call the variable cdnPath directly.
    public String getUploadPath() {
        if (cdnPath == null) return "";
        return cdnPath.replace("file:///", "");
    }

    public String getCdnForPublic() {
        if (cdnUrl == null || cdnPort == null || cdnPathAlias == null) return "";

        return cdnUrl +
                (cdnPort.equalsIgnoreCase("80") || cdnPort.equalsIgnoreCase("443") ? "" : ConstantData.COLON + cdnPort) +
                ConstantData.BACK_SLACK +
                cdnPathAlias;
    }

    public String getCdnIpForPublic() {
        return cdnUrl + (cdnPort.equalsIgnoreCase("80") || cdnPort.equalsIgnoreCase("443") ? "" : ConstantData.COLON + cdnPort);
    }
}