package org.effective_mobile.task_management_system.resource.task

import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.repository.PrivilegeRepository
import org.effective_mobile.task_management_system.database.repository.RoleRepository
import org.effective_mobile.task_management_system.database.repository.TaskRepository
import org.effective_mobile.task_management_system.database.repository.UserRepository
import org.effective_mobile.task_management_system.pojo.HasTaskId
import org.effective_mobile.task_management_system.resource.UserAndTaskIntegrationBase
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo
import org.effective_mobile.task_management_system.utils.Api
import org.effective_mobile.task_management_system.utils.Api.TASK
import org.effective_mobile.task_management_system.utils.enums.Priority
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletResponse

open class AbstractTaskResourceTest @Autowired constructor(
    userRepository: UserRepository,
    taskRepository: TaskRepository,
    privilegeRepository: PrivilegeRepository,
    roleRepository: RoleRepository
): UserAndTaskIntegrationBase(
    userRepository,
    taskRepository,
    privilegeRepository,
    roleRepository
) {
    protected fun createTaskUrl() = TASK
    protected fun setExecutorUrl(taskId: Long) = "$TASK/$taskId${Api.EXECUTOR}"
    protected fun editTaskUrl(l: Long) = "$TASK/$l"
    protected fun getTaskUrl(taskId: Long) = "$TASK/$taskId"
    protected fun setStatusUrl(taskId: Long) = "$TASK/$taskId/status"

    protected inline fun <reified R : HasTaskId> MockHttpServletResponse.getBodyAndTask(): Pair<R, Task> {
        val body = getBody<R>()
        val task = taskRepository.findOrThrow(Task::class.java, body.getTaskId())
        Assertions.assertEquals(200, status)
        return body to task
    }

    companion object {
        @JvmStatic protected fun prioritiesNames() = Priority.values().map { it.name }

        @JvmStatic protected fun taskEditing(create: TaskEditionRequestPojo.() -> Unit): TaskEditionRequestPojo {
            return TaskEditionRequestPojo().apply(create)
        }

        @JvmStatic protected fun taskCreation(create: TaskCreationRequestPojo.() -> Unit): TaskCreationRequestPojo {
            return TaskCreationRequestPojo().apply(create)
        }
    }
}
