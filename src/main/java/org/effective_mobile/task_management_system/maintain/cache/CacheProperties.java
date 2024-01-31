package org.effective_mobile.task_management_system.maintain.cache;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Log4j2
public class CacheProperties {

    public final Boolean appCacheEnabled;
    public final Collection<CacheSettings> settings;
    public CacheProperties(
        ObjectMapper objectMapper,
        @Value("#{${app.cache.enabled}}") Boolean appCacheEnabled,
        @Value("${app.cache.tasks.info}") String cacheTasksInfo,
        @Value("${app.cache.users.auth.info}") String cacheUsersAuthInfo,
        @Value("${app.cache.tasks.privileges.info}") String cachePrivilegesInfo,
        @Value("${app.cache.tasks.authorities.info}") String cacheAuthoritiesInfo
    ) throws JsonProcessingException {
        this.appCacheEnabled = appCacheEnabled;
        this.settings = List.of(
            readCacheInfo(objectMapper, cacheTasksInfo),
            readCacheInfo(objectMapper, cacheUsersAuthInfo),
            readCacheInfo(objectMapper, cachePrivilegesInfo),
            readCacheInfo(objectMapper, cacheAuthoritiesInfo)
        );

        for (CacheSettings setting : this.settings) {
            log.info(
                ("""
                    CACHE Settings [%s] was read:
                    \tenabled - %s;
                    \texpiration: %s %s
                    \tinitial capacity: %s
                    \t maximum size: %s
                """
                ).formatted(
                    setting.getName(),
                    setting.isEnabled() ? "yes" : "no",
                    setting.getTtl().getType(),
                    setting.getTtl().getValue(),
                    setting.getInitialCapacity(),
                    setting.getMaximumSize()
                )
            );
        }
    }

    private CacheSettings readCacheInfo(
        ObjectMapper objectMapper,
        String cacheTasksInfo
    ) throws JsonProcessingException {
        return objectMapper.readValue(cacheTasksInfo, CacheSettings.class);
    }
}