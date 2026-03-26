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
        registerMappers(hints);
        registerMapperDtos(hints);
    }

    private void registerMapperDtos(RuntimeHints hints) {
        hints.reflection().registerType(AppUserDto.class, MemberCategory.values());
        hints.reflection().registerType(FileManagerDto.class, MemberCategory.values());
        hints.reflection().registerType(FilesDirectoryDto.class, MemberCategory.values());
        hints.reflection().registerType(Permission.class, MemberCategory.values());
    }
    private void registerMappers(RuntimeHints hints) {
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Mapper.class));
        provider.findCandidateComponents(MAPPER_PACKAGE).forEach(bd -> {
            try {

                Class<?> mapperClass = Class.forName(bd.getBeanClassName());

                hints.proxies().registerJdkProxy(
                        mapperClass,
                        org.springframework.aop.SpringProxy.class,
                        org.springframework.aop.framework.Advised.class,
                        org.springframework.core.DecoratingProxy.class
                );

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        });
    }

}

