package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.mybatis.AccessTokenMybatis;
import com.bekaku.api.spring.mybatis.AppRoleMybatis;
import com.bekaku.api.spring.mybatis.AppUserMybatis;
import com.bekaku.api.spring.mybatis.FileManagerMybatis;
import com.bekaku.api.spring.mybatis.FilesDirectoryMybatis;
import com.bekaku.api.spring.mybatis.PermissionMybatis;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class AppHints implements RuntimeHintsRegistrar {

    private static final String MAPPER_PACKAGE = "com.bekaku.api.spring.mybatis";
    private static final String DTO_PACKAGE = "com.bekaku.api.spring.dto";
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
        hints.resources().registerPattern("log4j2.*\\.xml");

        // (ทางเลือก) ถ้าอยากเจาะจงเฉพาะนามสกุลไฟล์รูปภาพ เพื่อไม่ให้ไฟล์ขยะติดไปด้วย
        // hints.resources().registerPattern("static/img/**/*.png");
        // hints.resources().registerPattern("static/img/**/*.jpg");
        // hints.resources().registerPattern("static/img/**/*.svg");

        hints.reflection().registerType(
                org.apache.ibatis.logging.slf4j.Slf4jImpl.class,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
        );
//        registerMappers(hints);
    }

    private void registerMappers(RuntimeHints hints) {
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Mapper.class));
        provider.findCandidateComponents(MAPPER_PACKAGE).forEach(bd -> {
            try {
                Class<?> cls = Class.forName(bd.getBeanClassName());
                hints.reflection().registerType(cls,
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.INVOKE_DECLARED_METHODS);
                hints.proxies().registerJdkProxy(cls);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

