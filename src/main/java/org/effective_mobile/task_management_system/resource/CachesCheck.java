package org.effective_mobile.task_management_system.resource;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.effective_mobile.task_management_system.component.TaskComponent;
import org.effective_mobile.task_management_system.database.entity.AbstractEntity;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.resource.json.JsonPojoId;
import org.effective_mobile.task_management_system.service.TaskResponsePojoWithCacheInfo;
import org.effective_mobile.task_management_system.utils.converter.TaskConverter;
import org.hibernate.LazyInitializationException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Profile("dev")
@RequestMapping("/cache")
@AllArgsConstructor
public class CachesCheck {

    private CacheManager cacheManager;
    private TaskComponent taskComponent;

    @GetMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody Collection<Cache> getAllCache() {
        return cacheManager
            .getCacheNames()
            .stream()
            .map(name -> cacheManager.getCache(name))
            .collect(Collectors.toList());
    }

    @GetMapping("/{cache_name}/{id}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody Collection<TaskResponsePojoWithCacheInfo> getCachedEntityBy(
        @PathVariable(name = "cache_name") String cacheName,
        @PathVariable Long id
    ) {
        Cache cache = cacheManager.getCache(cacheName);

        if (cache == null) {
            throw new NoSuchElementException("There is no cache with name = '%s'".formatted(cacheName));
        }

        Task task = getTask(cache, id);
        return task == null ? List.of() : List.of(withRef(task));
    }

    @GetMapping("/{name}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody Collection<JsonPojoId> getCacheByName(
        @PathVariable String name
    ) {
        Cache cache = cacheManager.getCache(name);

        if (cache == null) {
            throw new NoSuchElementException("There is no cache with name = '%s'".formatted(name));
        }

        return taskComponent.getAll().stream()
            .map(AbstractEntity::getId)
            .map(it -> getTask(cache, it))
            .filter(Objects::nonNull)
            .map(this::withRef)
            .map(it -> ((JsonPojoId) it))
            .sorted(Comparator.comparing(JsonPojoId::getId))
            .toList();
    }


    private TaskResponsePojoWithCacheInfo withRef(Task task) {
        JsonPojoId taskResponsePojo;
        try {
             taskResponsePojo = TaskConverter.convert(task, true);
        } catch (LazyInitializationException e) {
             taskResponsePojo = new TaskResponsePojoWithCacheInfo.LazyExceptionTaskId(
                 task.getId(), task.getCreator().getId()
             );
        }

        return new TaskResponsePojoWithCacheInfo(
            ObjectUtils.identityToString(task),
            taskResponsePojo
        );
    }

    private Task getTask(Cache cache, Long it) {
        return cache.get(it, Task.class);
    }
}
