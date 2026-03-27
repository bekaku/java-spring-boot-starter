package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.mybatis.AccessTokenMybatis;
import com.bekaku.api.spring.mybatis.AppRoleMybatis;
import com.bekaku.api.spring.mybatis.AppUserMybatis;
import com.bekaku.api.spring.mybatis.FileManagerMybatis;
import com.bekaku.api.spring.mybatis.FilesDirectoryMybatis;
import com.bekaku.api.spring.mybatis.PermissionMybatis;
import org.apache.ibatis.executor.loader.cglib.CglibProxyFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
//@MapperScan("com.bekaku.api.spring.mybatis")
@EnableTransactionManagement
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // Set config โดยตรง
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
        config.setLazyLoadingEnabled(false);
        config.setAggressiveLazyLoading(false);
        config.setLogImpl(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
        config.setCacheEnabled(true);
        config.setDefaultStatementTimeout(3000);
        config.setMapUnderscoreToCamelCase(true);
        config.setUseGeneratedKeys(true);


        config.addMapper(AccessTokenMybatis.class);
        config.addMapper(AppRoleMybatis.class);
        config.addMapper(AppUserMybatis.class);
        config.addMapper(FileManagerMybatis.class);
        config.addMapper(FilesDirectoryMybatis.class);
        config.addMapper(PermissionMybatis.class);

        factoryBean.setConfiguration(config);
        // Set mapper locations
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath:mybatis/**/*.xml")
        );

        // Set type aliases package
        factoryBean.setTypeAliasesPackage("com.bekaku.api.spring.model");

        return factoryBean.getObject();
    }
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
