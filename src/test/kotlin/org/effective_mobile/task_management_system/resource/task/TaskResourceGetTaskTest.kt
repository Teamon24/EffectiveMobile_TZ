package org.effective_mobile.task_management_system.resource.task

import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import org.effective_mobile.task_management_system.AssertionsUtils
import org.effective_mobile.task_management_system.RandomTasks.task
import org.effective_mobile.task_management_system.RandomUsers.user
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.messages.EntityNotFoundMessages
import org.effective_mobile.task_management_system.pojo.HasTaskInfo
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod.*
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import java.util.stream.Stream

/**
 * Test for [TaskResource.getTask].
 */
class TaskResourceGetTaskTest: AbstractTaskResourceTest() {

    @ParameterizedTest
    @MethodSource("correctEntities")
    fun `getTask 200`(creator: User, executor: User?, task: Task) {
        executor?.let { saveAndFlush(it) }
        saveAllAndFlush(creator, task)

        creator {
            authenticated()
            send(mvc) {
                method = GET
                url = getTaskUrl(task.getTaskId())
            } response {
                val (body, task) = getBodyAndTask<TaskResponsePojo>()
                AssertionsUtils.assertEquals(task, body, HasTaskInfo.getters())
            }
        }
    }

    /**
     * 401 response code.
     */
    @ParameterizedTest
    @MethodSource("correctEntities")
    fun `getTask 401 authToken is absent`(creator: User, executor: User?, task: Task) {
        executor?.let { saveAndFlush(it) }
        saveAllAndFlush(creator, task)

        creator {
            send(mvc) {
                method = GET
                url = getTaskUrl(task.getTaskId())
            } response { requestInfo ->
                assert401(requestInfo)
            }
        }
    }

    /**
     * 404 response code.
     */
    @ParameterizedTest
    @MethodSource("correctEntities")
    fun `getTask 404 task is absent`(creator: User, executor: User?, task: Task) {
        executor?.let { saveAndFlush(it) }
        saveAllAndFlush(creator, task)

        val absentTaskId = Long.MAX_VALUE
        creator {
            authenticated()
            send(mvc) {
                method = GET
                url = getTaskUrl(absentTaskId)
            } response { requestInfo ->
                assert404(
                    requestInfo,
                    EntityNotFoundMessages.notFound(Task::class.java, absentTaskId),
                    JpaObjectRetrievalFailureException::class.canonicalName
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun correctEntities(): Stream<Arguments> {
            return stream {
                args {
                    val creator = +user() as User
                    val executor = +user() as User
                    +task(creator, executor)
                }

                args {
                    val creator = +user() as User
                    +null
                    +task(creator)
                }
            }
        }
    }
}

