package org.effective_mobile.task_management_system.resource.task

import home.CartesianProduct
import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import home.dsl.MutableListCreationDsl.flat
import home.extensions.BooleansExtensions.or
import home.extensions.BooleansExtensions.then
import home.extensions.CollectionsExtensions.exclude
import home.extensions.StringsExtensions.decapitalized
import org.apache.commons.lang3.StringUtils
import org.effective_mobile.task_management_system.AssertionsUtils
import org.effective_mobile.task_management_system.RandomTasks.content
import org.effective_mobile.task_management_system.RandomTasks.task
import org.effective_mobile.task_management_system.RandomUsers.user
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.database.repository.PrivilegeRepository
import org.effective_mobile.task_management_system.database.repository.RoleRepository
import org.effective_mobile.task_management_system.database.repository.TaskRepository
import org.effective_mobile.task_management_system.database.repository.UserRepository
import org.effective_mobile.task_management_system.exception.NothingToUpdateInTaskException
import org.effective_mobile.task_management_system.exception.messages.EntityNotFoundMessages
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages
import org.effective_mobile.task_management_system.exception.messages.ValidationMessages
import org.effective_mobile.task_management_system.pojo.HasTaskInfo
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo
import org.effective_mobile.task_management_system.utils.Api
import org.effective_mobile.task_management_system.utils.Constraints.Task.Content.Length
import org.effective_mobile.task_management_system.utils.JsonPojos
import org.effective_mobile.task_management_system.utils.enums.Priority
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.*
import org.springframework.http.HttpStatus
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import java.util.stream.Stream


/**
 * Test for [TaskResource.editTask].
 */
class TaskResourceEditTaskTest @Autowired constructor(
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

    data class TestDto(
        val taskPriority: Priority,
        val newPriority: String?,
        val newContent: String?,
    )

    @ParameterizedTest
    @MethodSource("validBodies")
    fun `editTask 200 valid edition data`(
        creator: User,
        task: Task,
        taskEditionRequestPojo: TaskEditionRequestPojo
    ) {
        saveAndFlush(creator)
        saveAndFlush(task)

        creator {
            send(mvc) {
                method = PUT
                url = editTaskUrl(task.getTaskId())
                body = taskEditionRequestPojo
            } response {
                val (body, foundTask) = getBodyAndTask<TaskResponsePojo>()
                AssertionsUtils.assertEquals(foundTask, body, HasTaskInfo.getters())

                if (taskEditionRequestPojo.content != null) {
                    Assertions.assertEquals(taskEditionRequestPojo.content, body.getContent())
                }

                if (taskEditionRequestPojo.priority != null) {
                    AssertionsUtils.assertEnumEquals(taskEditionRequestPojo.priority, body.getPriority())
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("sameTaskInfoBody")
    fun `editTask 400`(creator: User, task: Task, taskEditionRequestPojo: TaskEditionRequestPojo) {
        saveAllAndFlush(creator, task)

        creator {
            send(mvc) {
                method = PUT
                url = editTaskUrl(task.getTaskId())
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
    @MethodSource("emptyBodies")
    fun `editTask 400 empty body`(creator: User, task: Task, taskEditionRequestPojo: TaskEditionRequestPojo) {
        saveAllAndFlush(creator, task)

        creator {
            send(mvc) {
                method = PUT
                url = editTaskUrl(task.getTaskId())
                body = taskEditionRequestPojo
            } response { requestInfo ->
                assertValidationErrorInfo(requestInfo).also {
                    it.errors.apply {
                        Assertions.assertEquals(2, size)
                        it.assertValidationError(0) {
                            field =
                                JsonPojos.Task.Field.CONTENT
                            message = ValidationMessages.emptyBody()
                            rejectedValue = taskEditionRequestPojo.content
                            `object` = TaskEditionRequestPojo::class.java.simpleName.decapitalized
                        }

                        it.assertValidationError(1) {
                            field =
                                JsonPojos.Task.Field.PRIORITY
                            message = ValidationMessages.emptyBody()
                            rejectedValue = taskEditionRequestPojo.priority
                            `object` = TaskEditionRequestPojo::class.java.simpleName.decapitalized
                        }
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("invalidBodies")
    fun `editTask 400 invalid body`(creator: User, task: Task, taskEditionRequestPojo: TaskEditionRequestPojo) {
        saveAllAndFlush(creator, task)

        creator {
            send(mvc) {
                method = PUT
                url = editTaskUrl(task.getTaskId())
                body = taskEditionRequestPojo
            } response { requestInfo ->
                assertValidationErrorInfo(requestInfo).also { validationErrorInfo ->
                    validationErrorInfo.errors.apply {
                        Assertions.assertEquals(1, size)

                        validationErrorInfo.assertValidationError(0) {
                            taskEditionRequestPojo.apply {
                                field = content
                                    ?.isBlank()
                                    ?.then(JsonPojos.Task.Field.CONTENT)
                                    .or(JsonPojos.Task.Field.PRIORITY)

                                message = content.let {
                                    if (StringUtils.isBlank(it)) {
                                        ValidationMessages.invalidContent(it)
                                    } else {
                                        ValidationMessages.invalidPriority(priority)
                                    }
                                }
                                rejectedValue = content.let {
                                    if (StringUtils.isBlank(it)) content else priority
                                }
                                `object` = TaskEditionRequestPojo::class.java.simpleName.decapitalized
                            }
                        }
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("unathenticatedUser")
    fun `editTask 401`(
        creator: User,
        task: Task,
        taskEditionRequestPojo: TaskEditionRequestPojo
    ) {
        saveAllAndFlush(creator, task)
        creator {
            unauthenticated()
            send(mvc) {
                method = PUT
                url = editTaskUrl(task.getTaskId())
                body = taskEditionRequestPojo
            } response { requestInfo ->
                assert401(requestInfo)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("taskIsAbsent")
    fun `editTask 404 task is absent`(
        creator: User,
        taskEditionRequestPojo: TaskEditionRequestPojo
    ) {
        saveAllAndFlush(creator)
        val absentTaskId = Long.MAX_VALUE
        creator {
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
        fun validBodies(): Stream<Arguments> {
            val existedTaskPriorities = Priority.values().toList().sorted()
            return stream {
                existedTaskPriorities.forEach { existedTaskPriority ->
                    existedTaskPriority.also {
                        CartesianProduct.elements(
                            listOf(it),
                            validPriorities().exclude(it.name, it.name.lowercase()),
                            validContent()
                        )
                            .map(::toDto)
                            .filter(::notEmptyBody)
                            .forEach { dto -> argsUserTaskAndBody(dto) }
                    }
                }
            }
        }

        @JvmStatic
        fun invalidBodies(): Stream<Arguments> {
            val existedTaskPriorities = Priority.values().toList().sorted()
            return stream {
                existedTaskPriorities.forEach { existedTaskPriority ->
                    val priorityName = existedTaskPriority.name
                    val arguments =
                        CartesianProduct.elements(
                            listOf(existedTaskPriority),
                            invalidValues(),
                            validContent(),
                        ) +
                        CartesianProduct.elements(
                            listOf(existedTaskPriority),
                            validPriorities().exclude(priorityName, priorityName.lowercase()),
                            invalidValues(),
                        )
                    arguments
                        .map(::toDto)
                        .filter(::notEmptyBody)
                        .forEach { argsUserTaskAndBody(it) }
                }
            }
        }

        @JvmStatic
        fun emptyBodies(): Stream<Arguments> {
            return stream {
                Priority.values().forEach { existedTaskPriority ->
                    argsUserTaskAndBody(TestDto(existedTaskPriority, null, null))
                }
            }
        }

        @JvmStatic
        fun sameTaskInfoBody(): Stream<Arguments> {
            return stream {
                args {
                    val creator = + user() as User
                    val task = + task(creator) as Task
                    +taskEditing {
                        content = task.getContent();
                        priority = task.getPriority().name
                    }
                }
            }
        }

        @JvmStatic
        fun unathenticatedUser(): Stream<Arguments> {
            return stream {
                args {
                    val creator = +user() as User
                    val task = +task(creator) as Task
                    +taskEditing {
                        content = task.getContent();
                        priority = task.getPriority().name
                    }
                }
            }
        }

        @JvmStatic
        fun taskIsAbsent(): Stream<Arguments> {
            return stream {
                args {
                    val creator = +user() as User
                    val task = task(creator)
                    +taskEditing {
                        content = task.getContent();
                        priority = task.getPriority().name
                    }
                }
            }
        }

        private fun toDto(it: List<Any?>?): TestDto {
            return TestDto(it!![0] as Priority, it[1] as String?, it[2] as String?)
        }

        private fun notEmptyBody(testDto: TestDto): Boolean = testDto.newPriority != null && testDto.newContent != null

        private fun invalidValues() = listOf("", " ", "  ")

        private fun validPriorities() = flat {
            -prioritiesNames()
            -prioritiesNames().map { it.lowercase() }
            +null
        }

        private fun validContent() = listOf(content(Length.MAX), content(Length.MIN), null)

        private fun MutableList<Arguments>.argsUserTaskAndBody(args: TestDto) {
            val (existedTaskPriority, newPriority, newContent) = args
            args {
                val user = +user() as User
                +task(user, priority = existedTaskPriority)
                +taskEditing {
                    priority = newPriority
                    content = newContent
                }
            }
        }
    }
}

