package org.effective_mofile.task_management_system.resource

import home.dsl.DslContainer
import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import home.extensions.AnysExtensions.lowercaseFirstSimpleName
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.ValidationErrorInfo
import org.effective_mobile.task_management_system.pojo.HasTaskInfo
import org.effective_mobile.task_management_system.resource.Api
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo
import org.effective_mofile.task_management_system.AssertionsUtils
import org.effective_mofile.task_management_system.RandomTasks.content
import org.effective_mofile.task_management_system.RandomTasks.priority
import org.effective_mofile.task_management_system.RandomUsers
import org.effective_mofile.task_management_system.resource.TaskResourceTestUtils.taskCreation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod.*
import java.util.stream.Stream

/**
 * Test for [TaskResource.createTask].
 */
class TaskResourceCreateTaskTest : IntegrationTest() {

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
            authenticated()
            send(mvc) {
                method = POST
                url = Api.TASK
                body = taskCreationRequestPojo
            } response {
                val taskResponsePojo = getBody<TaskResponsePojo>()
                taskRepository.findOrThrow(Task::class.java, taskResponsePojo.id).apply {
                    AssertionsUtils.assertEquals(this, taskResponsePojo, HasTaskInfo.getters())
                    Assertions.assertEquals(taskCreationRequestPojo.content, taskResponsePojo.getContent())
                    AssertionsUtils.assertEnumEquals(
                        taskCreationRequestPojo.priority,
                        taskResponsePojo.getPriority()
                    )
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
            authenticated()
            send(mvc) {
                method = POST
                url = Api.TASK
                body = taskCreationRequestPojo
            } response {
                val validationErrorInfo = getBody<ValidationErrorInfo>()
                taskCreationRequestPojo.apply {
                    if (priority?.isBlank() ?: true) {
                        val priorityErrors = validationErrorInfo.errors.filter { it.field == "priority" }
                        assertEquals(1, priorityErrors.size)
                        assertEquals(priority, priorityErrors[0].rejectedValue)
                        assertEquals(lowercaseFirstSimpleName, priorityErrors[0].`object`)
                    }

                    if (content?.isBlank() ?: true) {
                        val contentErrors = validationErrorInfo.errors.filter { it.field == "content" }
                        assertEquals(1, contentErrors.size)
                        assertEquals(content, contentErrors[0].rejectedValue)
                        assertEquals(lowercaseFirstSimpleName, contentErrors[0].`object`)
                    }
                }
            }
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
            send(mvc) {
                method = POST
                url = Api.TASK
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
            return Stream.of(
                Arguments.of(
                    taskCreation { priority = priority(); content = content() }, creator
                )
            )
        }

        @JvmStatic
        fun notValidTaskCreationRequestPojo(): Stream<Arguments> {
            val creator = RandomUsers.user()
            val notValid = TaskResourceTestUtils.notValidFieldsForTaskCreation()
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

