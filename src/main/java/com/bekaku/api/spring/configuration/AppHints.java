package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.FileManagerDto;
import com.bekaku.api.spring.dto.FilesDirectoryDto;
import com.bekaku.api.spring.model.Permission;
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
    private static final String MODEL_PACKAGE = "com.bekaku.api.spring.model";

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("i18n/*.properties");
        hints.resources().registerPattern("i18n/*/*.properties");
        hints.resources().registerPattern("mybatis/**/*.xml");
        hints.resources().registerPattern("templates/**/*.html");
        hints.resources().registerPattern("acl.json");
        hints.resources().registerPattern("static/img/**/*");
        hints.resources().registerPattern("log4j2.*\\.xml");

        hints.reflection().registerType(
                org.apache.ibatis.logging.slf4j.Slf4jImpl.class,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
        );
        hints.reflection().registerType(
                org.apache.ibatis.session.Configuration.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
        );
        registerMappers(hints);
        registerPackageForReflection(hints, DTO_PACKAGE);
        registerPackageForReflection(hints, MODEL_PACKAGE);
    }

    private void registerPackageForReflection(RuntimeHints hints, String basePackage) {
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        for (BeanDefinition bd : provider.findCandidateComponents(basePackage)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());

                hints.reflection().registerType(clazz, MemberCategory.values());

            } catch (ClassNotFoundException e) {

                throw new RuntimeException(e);

            }
        }
    }

    private void registerMappers(RuntimeHints hints) {
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Mapper.class));
        provider.findCandidateComponents(MAPPER_PACKAGE).forEach(bd -> {
            try {

                Class<?> mapperClass = Class.forName(bd.getBeanClassName());

                hints.reflection().registerType(
                        mapperClass,
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.INVOKE_DECLARED_METHODS
                );
                hints.proxies().registerJdkProxy(mapperClass);

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        });
    }

}

