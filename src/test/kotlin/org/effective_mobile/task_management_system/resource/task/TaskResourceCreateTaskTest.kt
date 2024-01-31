package org.effective_mobile.task_management_system.resource.task

import home.CartesianProduct
import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import home.extensions.AnysExtensions.decapitalizedSimpleName
import org.effective_mobile.task_management_system.AssertionsUtils
import org.effective_mobile.task_management_system.RandomTasks.content
import org.effective_mobile.task_management_system.RandomTasks.priority
import org.effective_mobile.task_management_system.RandomUsers
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.database.repository.PrivilegeRepository
import org.effective_mobile.task_management_system.database.repository.RoleRepository
import org.effective_mobile.task_management_system.database.repository.TaskRepository
import org.effective_mobile.task_management_system.database.repository.UserRepository
import org.effective_mobile.task_management_system.exception.ValidationErrorInfo
import org.effective_mobile.task_management_system.pojo.HasTaskInfo
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo.*
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo
import org.effective_mobile.task_management_system.utils.JsonPojos.Task
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.*
import java.util.stream.Stream

/**
 * Test for [TaskResource.createTask].
 */
class TaskResourceCreateTaskTest @Autowired constructor(
    userRepository: UserRepository,
    taskRepository: TaskRepository,
    privilegeRepository: PrivilegeRepository,
    roleRepository: RoleRepository
): AbstractTaskResourceTest(
    userRepository,
    taskRepository,
    privilegeRepository,
    roleRepository
) {

    /**
     * 200 response code.
     */
    @ParameterizedTest
    @MethodSource("correctTaskCreationRequestPojo")
    fun `createTask 200`(
        taskCreationRequestPojo: TaskCreationRequestPojo,
        creator: User
    ) {
        saveAndFlush(creator)
        creator {
            send(mvc) {
                method = POST
                url = createTaskUrl()
                body = taskCreationRequestPojo
            } response {
                val (body, task) = getBodyAndTask<TaskResponsePojo>()
                task.apply {
                    AssertionsUtils.assertEquals(this, body, HasTaskInfo.getters())
                    AssertionsUtils.assertEnumEquals(taskCreationRequestPojo.priority, body.getPriority())
                    assertEquals(taskCreationRequestPojo.content, body.getContent())
                }
            }
        }
    }

    /**
     * 400 response code.
     */
    @ParameterizedTest
    @MethodSource("notValidTaskCreationRequestPojo")
    fun `createTask 400 not valid creation data`(
        taskCreationRequestPojo: TaskCreationRequestPojo,
        creator: User
    ) {
        saveAndFlush(creator)

        creator {
            send(mvc) {
                method = POST
                url = createTaskUrl()
                body = taskCreationRequestPojo
            } response {
                val validationErrorInfo = getBody<ValidationErrorInfo>()
                taskCreationRequestPojo.apply {
                    assertErrors(validationErrorInfo, priority,
                        Task.Field.PRIORITY
                    )
                    assertErrors(validationErrorInfo, content,
                        Task.Field.CONTENT
                    )
                }
            }
        }
    }

    private fun TaskCreationRequestPojo.assertErrors(
        validationErrorInfo: ValidationErrorInfo,
        field: String?,
        fieldName: String
    ) {
        if (field != null && field.isBlank()) {
            val fieldErrors = validationErrorInfo.errors.filter { it.field == fieldName }
            assertEquals(1, fieldErrors.size)
            assertEquals(field, fieldErrors[0].rejectedValue)
            assertEquals(this@assertErrors.decapitalizedSimpleName, fieldErrors[0].`object`)
        }
    }

    @ParameterizedTest
    @MethodSource("correctTaskCreationRequestPojo")
    fun `createTask 401 authToken is absent`(
        taskCreationRequestPojo: TaskCreationRequestPojo,
        creator: User
    ) {
        saveAndFlush(creator)
        creator {
            unauthenticated()
            send(mvc) {
                method = POST
                url = createTaskUrl()
                body = taskCreationRequestPojo
            } response { requestInfo ->
                assert401(requestInfo)
            }
        }
    }

    companion object {
        @JvmStatic
        fun correctTaskCreationRequestPojo(): Stream<Arguments> {
            val creator = RandomUsers.user()
            return stream {
                args {
                    + taskCreation { priority = priority(); content = content(30) }
                    + creator
                }
            }
        }

        @JvmStatic
        fun notValidTaskCreationRequestPojo(): Stream<Arguments> {
            val creator = RandomUsers.user()

            val notValid = CartesianProduct.elements(
                listOf(content(30), " ", "", null),
                listOf(priority(), " ", "", null)
            ).filter { args ->
                args.any { it == null || (it as String).isBlank() }
            }

            stream {
                notValid.forEach {
                    args {
                        +taskCreation { content = it[0] as String?; priority = it[1] as String? }
                        +creator
                    }
                }
            }.apply {
                return this
            }
        }
    }
}

