package org.effective_mobile.task_management_system.resource

import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import home.extensions.StringsExtensions.decapitalized
import org.effective_mobile.task_management_system.RandomTasks.task
import org.effective_mobile.task_management_system.RandomUsers.user
import org.effective_mobile.task_management_system.component.StatusChangeValidatorTest
import org.effective_mobile.task_management_system.component.validator.smart.ValidationMessages
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.DeniedOperationException
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException
import org.effective_mobile.task_management_system.resource.Api.TASK
import org.effective_mobile.task_management_system.resource.json.task.StatusChangeRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.StatusChangeResponsePojo
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.utils.enums.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus.*
import org.springframework.web.bind.MethodArgumentNotValidException
import java.util.*
import java.util.stream.Stream

/**
 * Test for [TaskResource.setStatus].
 */
class TaskResourceSetStatusTest : AbstractTaskResourceTest() {

    private fun statusChangeUrl(taskId: Long) = "$TASK/$taskId/status"
    private fun statusChangeBody(newStatus: String?) = StatusChangeRequestPojo(newStatus)

    @ParameterizedTest
    @MethodSource("correctStatusChange")
    fun `setStatus 200`(
        user: User,
        task: Task,
        validNewStatus: Status,
        saveEntities: (base: UserAndTaskIntegrationBase) -> Unit,
    ) {
        saveEntities(this)
        val oldStatus = task.getStatus()
        user {
            authenticated()
            send(mvc) {
                method = PUT
                url = statusChangeUrl(task.getTaskId())
                body = statusChangeBody(validNewStatus.name)
            } response {
                val (body, editedTask) = getBodyAndTask<StatusChangeResponsePojo>()
                editedTask.apply {
                    assertEquals(id, body.getTaskId())
                    assertEquals(oldStatus, body.oldStatus)
                    assertEquals(getStatus(), body.newStatus)
                }
            }
        }
    }



    @ParameterizedTest
    @MethodSource("incorrectStatusChange")
    fun `setStatus 400`(
        user: User,
        task: Task,
        newInvalidStatus: Status,
        expectedEx: IllegalStatusChangeException,
        saveEntities: (UserAndTaskIntegrationBase) -> Unit
    ) {
        saveEntities(this)
        user {
            authenticated()
            send(mvc) {
                method = PUT
                url = statusChangeUrl(task.getTaskId())
                body = statusChangeBody(newInvalidStatus.name)
            } response { requestInfo ->
                assert400(
                    requestInfo,
                    expectedEx.message!!,
                    expectedEx::class.canonicalName
                )
            }
        }
    }


    @ParameterizedTest
    @MethodSource("incorrectStatusChangePojoJson")
    fun `setStatus 400 invalid value at payload`(
        user: User,
        task: Task,
        invalidNewStatus: String?,
        expectedExMessage: String,
        saveEntities: (UserAndTaskIntegrationBase) -> Unit
    ) {
        saveEntities(this)
        user {
            authenticated()
            send(mvc) {
                method = PUT
                url = statusChangeUrl(task.getTaskId())
                body = statusChangeBody(invalidNewStatus)
            } response { requestInfo ->
                assertValidationErrorInfo(
                    requestInfo,
                    MethodArgumentNotValidException::class.canonicalName
                ).also { actual ->
                    assertEquals(1, actual.errors.size)
                    actual.assertValidationError(0) {
                        field = "status"
                        message = expectedExMessage
                        rejectedValue = invalidNewStatus
                        `object` = StatusChangeRequestPojo::class.java.simpleName.decapitalized
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("incorrectUser")
    fun `setStatus 403 wrong user`(
        user: User,
        task: Task,
        newValidStatus: Status,
        lazyExpectedException: (details: CustomUserDetails, task: Task) -> String,
        saveEntities: (UserAndTaskIntegrationBase) -> Unit
    ) {
        saveEntities(this)
        user {
            authenticated()
            customUserDetails.authorities = hashSetOf()
            val expectedEx = lazyExpectedException(customUserDetails, task)
            send(mvc) {
                method = PUT
                url = statusChangeUrl(task.getTaskId())
                body = statusChangeBody(newValidStatus.name)
            } response { requestInfo ->
                assert403(
                    requestInfo,
                    expectedEx,
                    DeniedOperationException::class.canonicalName
                )
            }
        }
    }


    companion object {
        @JvmStatic
        fun correctStatusChange(): Stream<Arguments> {
            return StatusChangeValidatorTest.validChangeAndValidUser()
        }

        @JvmStatic
        fun incorrectStatusChange(): Stream<Arguments> {
            return StatusChangeValidatorTest.invalidStatusChangeTestData()
        }

        @JvmStatic
        fun incorrectUser(): Stream<Arguments> {
            return StatusChangeValidatorTest.validChangeButNotValidUserTestData()
        }

        @JvmStatic
        fun incorrectStatusChangePojoJson(): Stream<Arguments> {
            return stream {
                listOf(null, "", " ", "   ")
                    .forEach { invalidNewStatus ->
                        args {
                            val user = +user() as User
                            val task = +task(user) as Task
                            +invalidNewStatus
                            +ValidationMessages.invalidStatus(invalidNewStatus)
                            +{ base: UserAndTaskIntegrationBase -> base.saveAllAndFlush(user, task) }
                        }
                    }
            }
        }
    }
}





