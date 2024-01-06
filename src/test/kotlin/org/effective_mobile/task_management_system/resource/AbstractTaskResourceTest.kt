package org.effective_mobile.task_management_system.resource

import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.pojo.HasTaskId
import org.junit.jupiter.api.Assertions
import org.springframework.mock.web.MockHttpServletResponse

open class AbstractTaskResourceTest: UserAndTaskIntegrationBase() {

    protected inline fun <reified R : HasTaskId> MockHttpServletResponse.getBodyAndTask(): Pair<R, Task> {
        val body = getBody<R>()
        val task = taskRepository.findOrThrow(Task::class.java, body.getTaskId())
        Assertions.assertEquals(200, status)
        return body to task
    }

}
