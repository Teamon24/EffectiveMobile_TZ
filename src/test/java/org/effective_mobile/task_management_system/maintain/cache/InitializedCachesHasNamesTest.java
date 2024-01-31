package org.effective_mobile.task_management_system.maintain.cache;

import org.effective_mobile.task_management_system.TaskManagementSystemApp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TaskManagementSystemApp.class)
@AutoConfigureMockMvc
@PropertySource(
    value = {
        "classpath:application.properties"
    },
    encoding = "UTF-8"
)
public class InitializedCachesHasNamesTest {

    @Autowired
    private CacheProperties cacheProperties;

    @Value("${app.cache.users.auth.name}")
    private String usersAuth;

    @Value("${app.cache.tasks.name}")
    private String tasks;

    @Value("${app.cache.privileges.name}")
    private String privileges;

    @Value("${app.cache.authorities.name}")
    private String authorities;

    @Test
    public void initializedCachesHasNamesTest() {
        List<String> expected = cacheProperties.settings.stream().map(CacheSettings::getName).toList();
        List<String> actual = List.of(this.tasks, usersAuth, privileges, authorities);
        Assertions.assertEquals(expected.size(), actual.size());
        Assertions.assertTrue(expected.containsAll(actual));
    }
}
