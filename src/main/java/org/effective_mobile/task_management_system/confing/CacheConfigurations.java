package org.effective_mobile.task_management_system.confing;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@EnableCaching
public class CacheConfigurations {

    public static final  String TASKS_CACHE = "tasks";

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(TASKS_CACHE);
    }
}