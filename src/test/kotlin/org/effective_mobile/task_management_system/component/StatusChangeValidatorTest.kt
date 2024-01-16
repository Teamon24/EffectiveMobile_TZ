package org.effective_mobile.task_management_system.component

import home.IndicesCartesianProduct.Companion.product
import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import home.dsl.MutableListCreationDsl.mutableList
import home.extensions.AnysExtensions.plus
import home.extensions.CollectionsExtensions.plus
import org.effective_mobile.task_management_system.RandomTasks.task
import org.effective_mobile.task_management_system.RandomUsers.user
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.exception.DeniedOperationException
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException
import org.effective_mobile.task_management_system.exception.messages.AccessExceptionMessages.notAExecutor
import org.effective_mobile.task_management_system.exception.messages.AuthorizationExceptionMessages
import org.effective_mobile.task_management_system.exception.messages.AuthorizationExceptionMessages.neitherCreatorOrExecutor
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages
import org.effective_mobile.task_management_system.resource.UserAndTaskIntegrationBase
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.utils.enums.Status
import org.effective_mobile.task_management_system.utils.enums.Status.ASSIGNED
import org.effective_mobile.task_management_system.utils.enums.Status.DONE
import org.effective_mobile.task_management_system.utils.enums.Status.EXECUTING
import org.effective_mobile.task_management_system.utils.enums.Status.NEW
import org.effective_mobile.task_management_system.utils.enums.Status.PENDING
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import java.util.stream.Stream

/**
 * Test for class [StatusChangeValidator]
 */
class StatusChangeValidatorTest : UserAndTaskIntegrationBase() {

    @Autowired
    private lateinit var validator: StatusChangeValidator

    /**
     * Test for [StatusChangeValidator.validateByRules].
     */
    @ParameterizedTest
    @MethodSource("invalidStatusChangeTestData")
    fun `case when task status change is invalid`(
        user: User,
        task: Task,
        newInvalidStatus: Status,
        expectedEx: IllegalStatusChangeException,
        saveEntities: (UserAndTaskIntegrationBase) -> Unit
    ) {
        saveEntities(this)
        user.authenticated()
        Assertions.assertThrows(expectedEx::class.java) {
            validator.validateByRules(customUserDetails, task, newInvalidStatus)
        }.let { ex ->
            Assertions.assertEquals(expectedEx.message, ex.message)
        }
    }

    /**
     * Test for [StatusChangeValidator.validateByRules].
     */
    @ParameterizedTest
    @MethodSource("validChangeButNotValidUserTestData")
    fun `case when task status change is valid but not a user`(
        user: User,
        task: Task,
        newValidStatus: Status,
        lazyExpectedExceptionMessage: (details: CustomUserDetails, task: Task) -> String,
        saveEntities: (UserAndTaskIntegrationBase) -> Unit
    ) {
        saveEntities(this)
        user.authenticated()
        val expectedExMessage = lazyExpectedExceptionMessage(customUserDetails, task);
        Assertions.assertThrows(DeniedOperationException::class.java) {
            validator.validateByRules(customUserDetails, task, newValidStatus)
        }.let { ex ->
            Assertions.assertEquals(expectedExMessage, ex.message)
        }
    }

    /**
     * Test for [StatusChangeValidator.validateByRules].
     */
    @ParameterizedTest
    @MethodSource("validChangeAndValidUser")
    fun `case when task status change and user are valid but no authorities`(
        user: User,
        task: Task,
        newValidStatus: Status,
        saveEntities: (UserAndTaskIntegrationBase) -> Unit
    ) {
        saveEntities(this)
        user.authenticated()
        customUserDetails.authorities = hashSetOf()

        val expectedEx = DeniedOperationException(
            AuthorizationExceptionMessages.cantChangeStatus(
                user.id,
                task.id,
                task.getStatus(),
                newValidStatus
            )
        )
        Assertions.assertThrows(expectedEx::class.java) {
            validator.validateByRules(customUserDetails, task, newValidStatus)
        }.let { ex ->
            Assertions.assertEquals(expectedEx.message, ex.message)
        }
    }

    /**
     * Test for [StatusChangeValidator.validateByRules].
     */
    @ParameterizedTest
    @MethodSource("validChangeAndValidUser")
    fun `case when status change validation is positive`(
        user: User,
        task: Task,
        newValidStatus: Status,
        saveEntities: (UserAndTaskIntegrationBase) -> Unit
    ) {
        saveEntities(this)
        user.authenticated()
        validator.validateByRules(customUserDetails, task, newValidStatus)
    }


    companion object {
        @JvmStatic
        fun invalidStatusChangeTestData(): Stream<Arguments> {
            val creator = user()
            val executor = user()
            val task = task(creator, executor)

            val saveAll = { base: UserAndTaskIntegrationBase ->
                for (it in (creator + executor + task)) {
                    base.saveAndFlush(it)
                }
            }

            return stream {
                product(
                    mutableList {
                        at(0) { creator to task }
                        at(1) { executor to task }
                    },
                    Status.values().toList(),
                    listOf(NEW, ASSIGNED)
                ).let {
                    it.forEach { args ->
                        args {
                            val userTask = args[0] as Pair<User, Task>
                            val user = +userTask.first
                            val task = +userTask.second as Task
                            val taskStatus = args[1] as Status
                            val invalidStatus = +args[2] as Status
                            task.setStatus(taskStatus)
                            +IllegalStatusChangeException(
                                when (args[2] as Status) {
                                    NEW -> TaskExceptionMessages.statusCantBeInitial()
                                    else -> TaskExceptionMessages.statusCantBeAssign()
                                }
                            )
                            +saveAll
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun validChangeButNotValidUserTestData(): Stream<Arguments> {

            fun saveAll(creator: User, executor: User, neither: User, task: Task) = {
                    base: UserAndTaskIntegrationBase ->
                listOf(creator, executor, neither, task).forEach { base.saveAllAndFlush(it) }
            }

            return stream {
                listOf(
                    ASSIGNED to EXECUTING,
                    EXECUTING to PENDING,
                    EXECUTING to DONE,
                    PENDING to EXECUTING,
                    PENDING to DONE
                ).onEach { statusChange ->
                    args {
                        val creator = user()
                        val executor = user()
                        val neither = user()
                        +creator
                        val task = +task(creator, executor, statusChange.first) as Task
                        val newStatus = +statusChange.second
                        +{ details: CustomUserDetails, task: Task -> notAExecutor(details, task.id) }
                        +saveAll(creator, executor, neither, task)
                    }
                }
                args {
                    val creator = user()
                    val executor = user()
                    val neither = user()
                    +neither
                    val task = +task(creator, executor, DONE) as Task
                    val newStatus = +PENDING
                    +{ details: CustomUserDetails, task: Task -> neitherCreatorOrExecutor(task.id, details.userId) }
                    +saveAll(creator, executor, neither, task)
                }
            }
        }

        @JvmStatic
        fun validChangeAndValidUser(): Stream<Arguments> {
            fun saveAll(creator: User, executor: User, task: Task) = { base: UserAndTaskIntegrationBase ->
                for (entity in (creator + executor + task)) {
                    base.saveAndFlush(entity)
                }
            }

            return stream {
                listOf(
                    ASSIGNED to EXECUTING,
                    EXECUTING to PENDING,
                    EXECUTING to DONE,
                    PENDING to EXECUTING,
                    PENDING to DONE
                ).onEach { statusChange ->
                    args {
                        val creator = user()
                        val executor = user()
                        +executor
                        val task = +task(creator, executor, statusChange.first) as Task
                        val newStatus = +statusChange.second
                        +saveAll(creator, executor, task)
                    }
                }
                    run {
                        val creator = user()
                        val executor = user()
                        listOf(creator, executor).forEach {
                            args {
                                +it
                                val task = +task(creator, executor, DONE) as Task
                                val newStatus = +PENDING
                                +saveAll(creator, executor, task)
                            }
                        }
                    }
            }
        }
    }
}