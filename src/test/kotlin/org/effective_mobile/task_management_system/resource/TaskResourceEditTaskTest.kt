package org.effective_mobile.task_management_system.resource

import org.effective_mobile.task_management_system.AssertionsUtils
import org.effective_mobile.task_management_system.RandomTasks
import org.effective_mobile.task_management_system.RandomTasks.content
import org.effective_mobile.task_management_system.RandomTasks.priority
import org.effective_mobile.task_management_system.RandomUsers
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.NothingToUpdateInTaskException
import org.effective_mobile.task_management_system.exception.messages.EntityNotFoundMessages
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages
import org.effective_mobile.task_management_system.pojo.HasTaskInfo
import org.effective_mobile.task_management_system.resource.TaskResourceTestUtils.taskEditing
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import java.util.stream.Stream

/**
 * Test for [TaskResource.editTask].
 */
class TaskResourceEditTaskTest : AbstractTaskResourceTest() {

    @ParameterizedTest
    @MethodSource("validTaskEditionRequestPojo")
    fun `editTask 200 valid edition data`(
        creator: User,
        task: Task,
        taskEditionRequestPojo: TaskEditionRequestPojo
    ) {
        saveAndFlush(creator)
        saveAndFlush(task)

        creator {
            authenticated()
            send(mvc) {
                method = PUT
                url = Api.TASK + "/${task.getTaskId()}"
                body = taskEditionRequestPojo
            } response {
                val (body, foundTask) = getBodyAndTask<TaskResponsePojo>()
                foundTask.apply {
                    AssertionsUtils.assertEquals(this, body, HasTaskInfo.getters())
                    AssertionsUtils.assertEnumEquals(taskEditionRequestPojo.priority, body.getPriority())
                    Assertions.assertEquals(taskEditionRequestPojo.content, body.getContent())
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("sameTaskInfo")
    fun `editTask 400`(creator: User, task: Task, taskEditionRequestPojo: TaskEditionRequestPojo) {
        saveAllAndFlush(creator, task)

        creator {
            authenticated()
            send(mvc) {
                method = PUT
                url = Api.TASK + "/${task.getTaskId()}"
                body = taskEditionRequestPojo
            } response { requestInfo ->
                assertAny400(
                    HttpStatus.BAD_REQUEST,
                    requestInfo,
                    TaskExceptionMessages.nothingToChange(task.id),
                    NothingToUpdateInTaskException::class.java.canonicalName
                )
            }
        }
    }

    @ParameterizedTest
    @MethodSource("sameTaskInfo")
    fun editTaskCode401Test(
        creator: User,
        task: Task,
        taskEditionRequestPojo: TaskEditionRequestPojo
    ) {
        saveAllAndFlush(creator, task)
        creator {
            send(mvc) {
                method = PUT
                url = Api.TASK + "/${task.getTaskId()}"
                body = taskEditionRequestPojo
            } response { requestInfo ->
                assert401(requestInfo)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("sameTaskInfo")
    fun `editTask 404 task is absent`(
        creator: User,
        task: Task,
        taskEditionRequestPojo: TaskEditionRequestPojo
    ) {
        saveAllAndFlush(creator)
        val absentTaskId = Long.MAX_VALUE
        creator {
            authenticated()
            send(mvc) {
                method = PUT
                url = Api.TASK + "/$absentTaskId"
                body = taskEditionRequestPojo
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
        fun validTaskEditionRequestPojo(): Stream<Arguments> {
            val validFieldsForTaskEdition = TaskResourceTestUtils.validFieldsForTaskEdition()
            return validFieldsForTaskEdition
                .stream()
                .map {
                    RandomUsers.user().let {
                        Arguments.of(
                            it, RandomTasks.task(it), taskEditing { priority = priority(); content = content() }
                        )
                    }
            }

        }

        @JvmStatic
        fun sameTaskInfo(): Stream<Arguments> {
            val creator = RandomUsers.user()
            val task = RandomTasks.task(creator)
            return Stream.of(Arguments.of(
                creator, task, taskEditing { content = task.getContent(); priority = task.getPriority().name }
            ))
        }
    }
}

