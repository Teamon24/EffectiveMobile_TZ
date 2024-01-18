package org.effective_mobile.task_management_system.resource

import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import home.extensions.AnysExtensions.lowercaseFirstSimpleName
import org.effective_mobile.task_management_system.AssertionsUtils
import org.effective_mobile.task_management_system.RandomTasks.content
import org.effective_mobile.task_management_system.RandomTasks.priority
import org.effective_mobile.task_management_system.RandomUsers
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.ValidationErrorInfo
import org.effective_mobile.task_management_system.pojo.HasTaskInfo
import org.effective_mobile.task_management_system.resource.TaskResourceTestUtils.taskCreation
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo.*
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod.*
import java.util.stream.Stream

/**
 * Test for [TaskResource.createTask].
 */
class TaskResourceCreateTaskTest : AbstractTaskResourceTest() {

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
            authenticated()
            send(mvc) {
                method = POST
                url = Api.TASK
                body = taskCreationRequestPojo
            } response {
                val validationErrorInfo = getBody<ValidationErrorInfo>()
                taskCreationRequestPojo.apply {
                    assertErrors(validationErrorInfo, priority, PRIORITY_FIELD_NAME)
                    assertErrors(validationErrorInfo, content, CONTENT_FIELD_NAME)
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
            assertEquals(this@assertErrors.lowercaseFirstSimpleName, fieldErrors[0].`object`)
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

