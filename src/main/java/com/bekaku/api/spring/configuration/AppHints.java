package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.mybatis.AccessTokenMybatis;
import com.bekaku.api.spring.mybatis.AppRoleMybatis;
import com.bekaku.api.spring.mybatis.AppUserMybatis;
import com.bekaku.api.spring.mybatis.FileManagerMybatis;
import com.bekaku.api.spring.mybatis.FilesDirectoryMybatis;
import com.bekaku.api.spring.mybatis.PermissionMybatis;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class AppHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // บอกให้ GraalVM ก๊อปปี้ทุกไฟล์ในโฟลเดอร์ i18n เข้าไปใน Native Image ด้วย
        hints.resources().registerPattern("i18n/*.properties");
        hints.resources().registerPattern("i18n/*/*.properties");

        // 2. ไฟล์ MyBatis XML
        // ใช้ ** เพื่อให้ครอบคลุมไฟล์ .xml ในโฟลเดอร์ mybatis และโฟลเดอร์ย่อยทั้งหมด
        hints.resources().registerPattern("mybatis/**/*.xml");

        // 3. ไฟล์ HTML Templates (เช่น Thymeleaf หรือ Freemarker)
        hints.resources().registerPattern("templates/**/*.html");

        // 4. ไฟล์ JSON ที่อยู่ชั้นนอกสุดของโฟลเดอร์ resources
        hints.resources().registerPattern("acl.json");

        // 5. ไฟล์รูปภาพในโฟลเดอร์ static/img/
        // ใช้ **/* เพื่อก๊อปปี้ทุกไฟล์ (ไม่สนนามสกุล) ที่อยู่ใน img และโฟลเดอร์ย่อย
        hints.resources().registerPattern("static/img/**/*");

        // (ทางเลือก) ถ้าอยากเจาะจงเฉพาะนามสกุลไฟล์รูปภาพ เพื่อไม่ให้ไฟล์ขยะติดไปด้วย
        // hints.resources().registerPattern("static/img/**/*.png");
        // hints.resources().registerPattern("static/img/**/*.jpg");
        // hints.resources().registerPattern("static/img/**/*.svg");
        hints.reflection().registerType(
                org.apache.ibatis.logging.slf4j.Slf4jImpl.class,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
        );
        hints.proxies().registerJdkProxy(
                AccessTokenMybatis.class
        );
        hints.proxies().registerJdkProxy(
                AppRoleMybatis.class
        );
        hints.proxies().registerJdkProxy(
                AppUserMybatis.class
        );
        hints.proxies().registerJdkProxy(
                FileManagerMybatis.class
        );
        hints.proxies().registerJdkProxy(
                FilesDirectoryMybatis.class
        );
        hints.proxies().registerJdkProxy(
                PermissionMybatis.class
        );
    }
}

