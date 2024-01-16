package org.effective_mobile.task_management_system.resource

import org.effective_mobile.task_management_system.component.StatusChangeValidatorTest
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.DeniedOperationException
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException
import org.effective_mobile.task_management_system.resource.Api.TASK
import org.effective_mobile.task_management_system.resource.json.task.StatusChangeRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.StatusChangeResponsePojo
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.utils.enums.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus
import java.util.stream.Stream

/**
 * Test for [TaskResource.setExecutor].
 */
class TaskResourceSetStatusTest : AbstractTaskResourceTest() {

    private fun statusChangeUrl(taskId: Long) = "$TASK/$taskId/status"
    private fun statusChangeBody(newStatus: Status) = StatusChangeRequestPojo(newStatus.name)

    @ParameterizedTest
    @MethodSource("correctStatusChange")
    fun `setStatus 200`(
        user: User,
        task: Task,
        newStatus: Status,
        saveEntities: (base: UserAndTaskIntegrationBase) -> Unit,
    ) {
        saveEntities(this)
        val oldStatus = task.getStatus()
        user {
            authenticated()
            send(mvc) {
                method = PUT
                url = statusChangeUrl(task.getTaskId())
                body = statusChangeBody(newStatus)
            } response {
                val (body, editedTask) = getBodyAndTask<StatusChangeResponsePojo>()
                editedTask.apply {
                    Assertions.assertEquals(id, body.getTaskId())
                    Assertions.assertEquals(oldStatus, body.oldStatus)
                    Assertions.assertEquals(getStatus(), body.newStatus)
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
                body = statusChangeBody(newInvalidStatus)
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
                body = statusChangeBody(newValidStatus)
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
    }
}


