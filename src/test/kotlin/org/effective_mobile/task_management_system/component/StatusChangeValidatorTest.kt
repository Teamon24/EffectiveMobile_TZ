package org.effective_mobile.task_management_system.component

import home.CartesianProduct
import home.dsl.JUnit5ArgumentsDsl.args
import home.dsl.JUnit5ArgumentsDsl.stream
import home.extensions.AnysExtensions.plus
import home.extensions.CollectionsExtensions.plus
import org.effective_mobile.task_management_system.RandomTasks.task
import org.effective_mobile.task_management_system.RandomUsers.user
import org.effective_mobile.task_management_system.database.entity.Role
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.database.repository.PrivilegeRepository
import org.effective_mobile.task_management_system.database.repository.RoleRepository
import org.effective_mobile.task_management_system.database.repository.TaskRepository
import org.effective_mobile.task_management_system.database.repository.UserRepository
import org.effective_mobile.task_management_system.exception.DeniedOperationException
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException
import org.effective_mobile.task_management_system.exception.messages.AccessExceptionMessages.notAExecutor
import org.effective_mobile.task_management_system.exception.messages.AuthorizationExceptionMessages
import org.effective_mobile.task_management_system.exception.messages.AuthorizationExceptionMessages.neitherCreatorOrExecutor
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages
import org.effective_mobile.task_management_system.resource.UserAndTaskIntegrationBase
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.utils.StatusChange
import org.effective_mobile.task_management_system.utils.enums.Status
import org.effective_mobile.task_management_system.utils.enums.Status.ASSIGNED
import org.effective_mobile.task_management_system.utils.enums.Status.DONE
import org.effective_mobile.task_management_system.utils.enums.Status.EXECUTING
import org.effective_mobile.task_management_system.utils.enums.Status.NEW
import org.effective_mobile.task_management_system.utils.enums.Status.PENDING
import org.effective_mobile.task_management_system.utils.enums.UserRole
import org.effective_mobile.task_management_system.utils.enums.UserRole.CREATOR
import org.effective_mobile.task_management_system.utils.enums.UserRole.EXECUTOR
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import java.util.stream.Stream

/**
 * Test for class [StatusChangeValidator]
 */
class StatusChangeValidatorTest @Autowired constructor(
    userRepository: UserRepository,
    taskRepository: TaskRepository,
    privilegeRepository: PrivilegeRepository,
    roleRepository: RoleRepository
): UserAndTaskIntegrationBase(
    userRepository,
    taskRepository,
    privilegeRepository,
    roleRepository
) {

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
        user.unauthorized()

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
                CartesianProduct.elements(
                    mutableListOf(creator to task, executor to task),
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
                    EXECUTING to DONE,
                    PENDING to EXECUTING
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

                run {
                    val creator = user()
                    val executor = user()
                    CartesianProduct.elements(
                        listOf(EXECUTING to PENDING, DONE to PENDING, PENDING to DONE),
                        listOf(creator, executor)
                    ).forEach { userAndChange ->
                        val change = userAndChange[0] as StatusChange
                        val user = userAndChange[1] as User
                        val neither = user()
                        args {
                            +neither
                            val task = +task(creator, executor, change.first) as Task
                            val newStatus = +change.second
                            +{ details: CustomUserDetails,
                               task: Task -> neitherCreatorOrExecutor(task.id, details.userId)
                            }
                            +saveAll(creator, executor, neither, task)
                        }
                    }
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
                   EXECUTING to DONE,
                   PENDING to EXECUTING
                ).onEach { statusChange ->
                    args {
                        val creator = user(roles = mutableListOf(creatorRole))
                        val executor = user(roles = mutableListOf(executorRole))
                        +executor
                        val task = +task(creator, executor, statusChange.first) as Task
                        val newStatus = +statusChange.second
                        +saveAll(creator, executor, task)
                    }
                }
                    run {
                        CartesianProduct.elements(
                            listOf(EXECUTING to PENDING, PENDING to DONE, DONE to PENDING),
                            listOf(creatorRole, executorRole)
                        ).forEach { userAndChange ->
                            val change = userAndChange[0] as StatusChange
                            val role = userAndChange[1] as Role
                            args {
                                var creator = user()
                                var executor = user()
                                role.isCreator {
                                    creator = user(roles = mutableListOf(role))
                                    +creator
                                }

                                role.isExecutor {
                                    executor = user(roles = mutableListOf(role))
                                    +executor
                                }

                                val task = +task(creator, executor, change.first) as Task
                                val newStatus = +change.second
                                +saveAll(creator, executor, task)
                            }
                        }
                    }
            }
        }

        private inline fun Role.isCreator(block: () -> Unit) = isRole(CREATOR, block)
        private inline fun Role.isExecutor(block: () -> Unit) = isRole(EXECUTOR, block)

        private inline fun Role.isRole(userRole: UserRole, block: () -> Unit) {
            if (this.name == userRole) block()
        }
    }
}