package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.mybatis.AccessTokenMybatis;
import com.bekaku.api.spring.mybatis.AppRoleMybatis;
import com.bekaku.api.spring.mybatis.AppUserMybatis;
import com.bekaku.api.spring.mybatis.FileManagerMybatis;
import com.bekaku.api.spring.mybatis.FilesDirectoryMybatis;
import com.bekaku.api.spring.mybatis.PermissionMybatis;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration(proxyBeanMethods = false)
//@MapperScan("com.bekaku.api.spring.mybatis")
@EnableTransactionManagement
public class MyBatisConfig {
    @Bean
    public AccessTokenMybatis accessTokenMybatis(SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperFactoryBean<AccessTokenMybatis> factoryBean = new MapperFactoryBean<>(AccessTokenMybatis.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean.getObject();
    }

    @Bean
    public AppRoleMybatis appRoleMybatis(SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperFactoryBean<AppRoleMybatis> factoryBean = new MapperFactoryBean<>(AppRoleMybatis.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean.getObject();
    }

    @Bean
    public AppUserMybatis appUserMybatis(SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperFactoryBean<AppUserMybatis> factoryBean = new MapperFactoryBean<>(AppUserMybatis.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean.getObject();
    }

    @Bean
    public FileManagerMybatis fileManagerMybatis(SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperFactoryBean<FileManagerMybatis> factoryBean = new MapperFactoryBean<>(FileManagerMybatis.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean.getObject();
    }

    @Bean
    public FilesDirectoryMybatis filesDirectoryMybatis(SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperFactoryBean<FilesDirectoryMybatis> factoryBean = new MapperFactoryBean<>(FilesDirectoryMybatis.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean.getObject();
    }

    @Bean
    public PermissionMybatis permissionMybatis(SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperFactoryBean<PermissionMybatis> factoryBean = new MapperFactoryBean<>(PermissionMybatis.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);
        return factoryBean.getObject();
    }
}
