package com.bekaku.api.spring.configuration;


import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

@Configuration
public class CacheConfig {
    @Bean
    public org.springframework.cache.CacheManager cacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager jCacheManager = provider.getCacheManager();
        return new JCacheCacheManager(jCacheManager);
    }
}
