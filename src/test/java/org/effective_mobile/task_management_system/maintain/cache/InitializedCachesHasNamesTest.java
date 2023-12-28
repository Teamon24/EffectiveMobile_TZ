package org.effective_mobile.task_management_system.maintain.cache;

import org.effective_mobile.task_management_system.TaskManagementSystemApp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TaskManagementSystemApp.class)
@AutoConfigureMockMvc
public class InitializedCachesHasNamesTest {

    @Autowired
    private CacheProperties cacheProperties;

    @Test
    public void initializedCachesHasNamesTest() {
        Assertions.assertIterableEquals(
            List.of(AppCacheNames.TASKS, AppCacheNames.USERS_AUTH),
            cacheProperties.settings.stream().map(CacheSettings::getName).toList()
        );
    }
}
