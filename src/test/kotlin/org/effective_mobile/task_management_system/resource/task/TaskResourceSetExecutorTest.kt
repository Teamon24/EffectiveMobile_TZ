package org.effective_mobile.task_management_system.resource.task

import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import jakarta.persistence.EntityNotFoundException
import org.effective_mobile.task_management_system.RandomTasks.task
import org.effective_mobile.task_management_system.RandomUsers.user
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.AssignmentException
import org.effective_mobile.task_management_system.exception.DeniedOperationException
import org.effective_mobile.task_management_system.exception.messages.AccessExceptionMessages
import org.effective_mobile.task_management_system.exception.messages.EntityNotFoundMessages
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages
import org.effective_mobile.task_management_system.exception.messages.UserExceptionMessages
import org.effective_mobile.task_management_system.resource.IntegrationTest
import org.effective_mobile.task_management_system.resource.json.assignment.AssignmentRequestPojo
import org.effective_mobile.task_management_system.resource.json.assignment.AssignmentResponsePojo
import org.effective_mobile.task_management_system.utils.enums.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import java.util.stream.Stream

/**
 * Test for [TaskResource.setExecutor].
 */
class TaskResourceSetExecutorTest : AbstractTaskResourceTest() {

    private fun editTaskBody(newExecutor: User) = AssignmentRequestPojo(newExecutor.getUsername())

    @ParameterizedTest
    @MethodSource("correctEntities")
    fun `setExecutor 200`(
        creator: User,
        oldExecutor: User?,
        newExecutor: User,
        task: Task
    ) {
        initializeEntities(creator, oldExecutor, newExecutor, task)

        val oldExecutorUsername = task.getExecutorUsername()

        creator {
            authenticated()
            send(mvc) {
                method = PUT
                url = setExecutorUrl(task.getTaskId())
                body = editTaskBody(newExecutor)
            } response {
                val (body, editedTask) = getBodyAndTask<AssignmentResponsePojo>()
                editedTask.apply {
                    Assertions.assertEquals(id, body.getTaskId())
                    Assertions.assertEquals(getStatus(), body.status)
                    Assertions.assertEquals(getStatus(), Status.ASSIGNED)
                    Assertions.assertEquals(executor.getUsername(), body.newExecutorUsername)
                    Assertions.assertEquals(oldExecutorUsername, body.oldExecutorUsername)
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("sameExecutorChange")
    fun `setExecutor 400 same executor change`(
        creator: User,
        oldExecutor: User?,
        newExecutor: User,
        task: Task
    ) {
        initializeEntities(creator, oldExecutor, newExecutor, task)
        creator {
            authenticated()
            send(mvc) {
                method = PUT
                url = setExecutorUrl(task.getTaskId())
                body = editTaskBody(newExecutor)
            } response { requestInfo ->
                assertAny400(
                    HttpStatus.BAD_REQUEST,
                    requestInfo,
                    TaskExceptionMessages.sameExecutorChange(task.id, newExecutor.getUsername()),
                    AssignmentException::class.java.canonicalName
                )
            }
        }
    }

    @ParameterizedTest
    @MethodSource("correctEntities")
    fun `setExecutor 401 not authenticated`() {
        val creator = user()
        val task = task(creator)
        val newExecutor = user()
        saveAllAndFlush(creator, task, newExecutor)
        creator {
            send(mvc) {
                method = PUT
                url = setExecutorUrl(task.getTaskId())
                body = editTaskBody(newExecutor)
            } response { requestInfo ->
                assert401(requestInfo)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("correctEntities")
    fun `setExecutor 403 not creator access`(
        creator: User,
        oldExecutor: User?,
        newExecutor: User,
        task: Task
    ) {
        initializeEntities(creator, oldExecutor, newExecutor, task)
        newExecutor {
            authenticated()
            send(mvc) {
                method = PUT
                url = setExecutorUrl(task.getTaskId())
                body = editTaskBody(newExecutor)
            } response { requestInfo ->
                assertAny400(
                    HttpStatus.FORBIDDEN,
                    requestInfo,
                    AccessExceptionMessages.notACreator(customUserDetails, task.id),
                    DeniedOperationException::class.java.canonicalName
                )
            }
        }
    }

    @Test
    fun `setExecutor 404 task is absent`() {
        val newExecutor = user()
        val creator = user()
        saveAllAndFlush(creator, newExecutor, task(creator))
        val absentTaskId = Long.MAX_VALUE
        creator {
            authenticated()
            send(mvc) {
                method = PUT
                url = setExecutorUrl(absentTaskId)
                body = editTaskBody(newExecutor)
            } response { requestInfo ->
                assertAny400(
                    HttpStatus.NOT_FOUND,
                    requestInfo,
                    EntityNotFoundMessages.notFound(Task::class.java, absentTaskId),
                    JpaObjectRetrievalFailureException::class.java.canonicalName
                )
            }
        }
    }

    @Test
    fun `setExecutor 404 executor is absent`() {
        val creator = user()
        val newExecutor = user()
        val task = task(creator)
        saveAllAndFlush(creator, task)
        creator {
            authenticated()
            send(mvc) {
                method = PUT
                url = setExecutorUrl(task.getTaskId())
                body = editTaskBody(newExecutor)
            } response { requestInfo ->
                assertAny400(
                    HttpStatus.NOT_FOUND,
                    requestInfo,
                    UserExceptionMessages.notFound(newExecutor.getUsername()),
                    EntityNotFoundException::class.java.canonicalName
                )
            }
        }
    }

    private fun IntegrationTest.initializeEntities(
        creator: User,
        oldExecutor: User?,
        newExecutor: User,
        task: Task
    ) {
        saveAndFlush(creator)
        oldExecutor?.let { saveAndFlush(oldExecutor) }
        saveAndFlush(newExecutor)
        saveAndFlush(task)
    }


    companion object {
        @JvmStatic
        fun correctEntities(): Stream<Arguments> {
            return stream {
                args {
                    val creator     = +user() as User
                    val oldExecutor = +user() as User
                    val newExecutor = +user()
                    val task        = +task(creator, oldExecutor)
                }

                args {
                    val creator = +user() as User
                    +null
                    +user()
                    +task(creator)
                }
            }
        }

        @JvmStatic
        fun sameExecutorChange(): Stream<Arguments> {
            return stream {
                args {
                    val (creator, oldExecutor) = user() to user()
                    val newExecutor = oldExecutor

                    + creator
                    + oldExecutor
                    + newExecutor
                    + task(creator, oldExecutor)
                }
            }
        }
    }
}


