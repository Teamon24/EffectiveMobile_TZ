package org.effective_mobile.task_management_system.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.effective_mobile.task_management_system.maintain.cache.CacheProperties;
import org.effective_mobile.task_management_system.maintain.cache.CacheSettings;
import org.effective_mobile.task_management_system.pojo.TimeToLiveInfo;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfigurations {

    @Bean
    public CacheManager cacheManager(final CacheProperties cacheProperties) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        if (!cacheProperties.appCacheEnabled) {
            return new NoOpCacheManager();
        }
        for (CacheSettings setting : cacheProperties.settings) {
            if (setting.isEnabled()) {
                TimeToLiveInfo timeToLiveInfo = setting.getTtl();

                Cache<Object, Object> cache =
                    Caffeine.newBuilder()
                        .initialCapacity(50)
                        .maximumSize(100)
                        .expireAfterAccess(timeToLiveInfo.getValue(), timeToLiveInfo.getType())
                        .build();

                cacheManager.registerCustomCache(setting.getName(), cache);
            }
        }
        return cacheManager;
    }
}
