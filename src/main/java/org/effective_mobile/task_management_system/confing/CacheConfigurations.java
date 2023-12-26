package org.effective_mobile.task_management_system.confing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfigurations {

    public static final  String TASKS_CACHE = "tasks";

    @Bean
    public CacheManager cacheManager(@Value("${app.cache.enabled}") String enableCaching) {
        if (enableCaching.equals("true")) {
            return new ConcurrentMapCacheManager(TASKS_CACHE);
        }
        return new NoOpCacheManager();
    }
}