package org.effective_mobile.task_management_system.component;

import org.effective_mobile.task_management_system.TaskManagementSystemApp;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TaskManagementSystemApp.class)
@AutoConfigureMockMvc
public class CacheTest {
}
