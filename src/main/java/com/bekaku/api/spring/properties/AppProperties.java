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
        QueueConfig queue,
        String cdnPath,
        String cdnPathAlias,
        String url,
        String port,
        String cdnUrl,
        String cdnPort,
        List<String> allowMimes         // app.allow-mimes
) {

    // 2. วิธีจัดการกับ Default Value (แทนที่ = new ArrayList<>())
    // เราใช้ Compact Constructor เพื่อเช็กว่าถ้า Spring ไม่ได้ Bind ค่ามาให้ ให้เป็น List ว่าง
    public AppProperties {
        if (menus == null) {
            menus = List.of(); // หรือจะใช้ new java.util.ArrayList<>() ก็ได้ครับ
        }
        if (defaultRecipients == null) {
            defaultRecipients = List.of();
        }
        if (allowMimes == null) {
            allowMimes = List.of();
        }
    }

    // 3. Custom Methods สามารถเขียนไว้ข้างใน Body ได้เลย
    // สังเกตว่าเราเลิกใช้ getCdnPath() แล้วเปลี่ยนมาเรียกชื่อตัวแปร cdnPath ตรงๆ ได้เลย
    public String getUploadPath() {
        if (cdnPath == null) return ""; // ดัก Null ไว้หน่อยเพื่อความปลอดภัยครับ
        return cdnPath.replace("file:///", "");
    }

    public String getCdnForPublic() {
        if (cdnUrl == null || cdnPort == null || cdnPathAlias == null) return "";

        return cdnUrl +
                (cdnPort.equalsIgnoreCase("80") || cdnPort.equalsIgnoreCase("443") ? "" : ConstantData.COLON + cdnPort) +
                ConstantData.BACK_SLACK +
                cdnPathAlias;
    }
}