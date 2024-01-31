package org.effective_mobile.task_management_system.resource

import org.effective_mobile.task_management_system.database.entity.AbstractEntity
import org.effective_mobile.task_management_system.database.entity.Privilege
import org.effective_mobile.task_management_system.database.entity.Role
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.database.repository.PrivilegeRepository
import org.effective_mobile.task_management_system.database.repository.RoleRepository
import org.effective_mobile.task_management_system.database.repository.TaskRepository
import org.effective_mobile.task_management_system.database.repository.UserRepository
import org.effective_mobile.task_management_system.utils.enums.UserRole
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class UserAndTaskIntegrationBase @Autowired constructor(
     val userRepository: UserRepository,
     val taskRepository: TaskRepository,
     val privilegeRepository: PrivilegeRepository,
     val roleRepository: RoleRepository
) : IntegrationTest() {
    init {
        UserAndTaskIntegrationBase.privilegeRepository = this.privilegeRepository
        UserAndTaskIntegrationBase.roleRepository = this.roleRepository
    }

    @AfterEach
    fun clearDb() {
        taskRepository.deleteAll()
        userRepository.deleteAll()
    }

    internal inline fun <reified T : AbstractEntity> saveAllAndFlush(vararg entities: T) =
        entities.forEach { saveAndFlush(it) }

    internal inline fun <reified T : AbstractEntity> saveAndFlush(entity: T): T {
        return when (entity) {
            is Role -> roleRepository.saveAndFlush(entity)
            is Privilege -> privilegeRepository.saveAndFlush(entity)
            is User -> userRepository.saveAndFlush(entity)
            is Task -> taskRepository.saveAndFlush(entity)
            else -> throw UnsupportedOperationException("There is no logic (#saveAndFlush) for entity ${T::class.java}")
        }
    }

    companion object {
        private const val statusChange = "TASK_STATUS_CHANGE"
        private const val statusSuspending = "STATUS_SUSPENDING"
        private const val statusExecuting = "STATUS_EXECUTING"
        private const val statusFinishing = "STATUS_FINISHING"
        private const val statusResuming = "STATUS_RESUMING"
        private const val statusSuspendedFinishing = "STATUS_SUSPENDED_FINISHING"
        private const val statusFinishedSuspending = "STATUS_FINISHED_SUSPENDING"

        private val statusChangeSubprivileges = listOf(
            statusExecuting,
            statusSuspending,
            statusFinishing,
            statusResuming,
            statusSuspendedFinishing,
            statusFinishedSuspending
        )

        private val creatorPrivileges = listOf(
            statusSuspending,
            statusSuspendedFinishing,
            statusFinishedSuspending
        )

        private var privilegeRepository: PrivilegeRepository? = null
        private var roleRepository: RoleRepository? = null

        var privilegesNotInitialized = true

        val creatorRole: Role by lazy {
            initPrivileges()
            role(UserRole.CREATOR.value) {
                creatorPrivileges.forEach { privileges.add(it.find()) }
            }
        }

        val executorRole: Role by lazy {
            initPrivileges()
            role(UserRole.EXECUTOR.value) {
                privileges.add(statusChange.find())
            }
        }

        private fun initPrivileges() {
            if (privilegesNotInitialized) {
                privilege(statusChange) {
                    statusChangeSubprivileges.forEach { child(it) }
                }
                privilegesNotInitialized = !privilegesNotInitialized
            }
        }

        private inline fun role(roleName: String, create: Role.() -> Unit = {}): Role {
            return Role().apply {
                name = UserRole.convert(roleName)
                create()
                persist()
            }
        }

        private inline fun privilege(privilegeName: String, create: Privilege.() -> Unit = {}): Privilege {
            return Privilege().apply {
                name = privilegeName
                persist()
                create()
            }
        }

        private inline fun Privilege.child(childName: String, create: Privilege.() -> Unit = {}): Privilege {
            val parent = this
            return Privilege().apply {
                name = childName
                parentId = parent.id
                persist()
                create()
            }
        }


        private fun String.find(): Privilege = privilegeRepository!!
            .findByName(this)
            .orElseThrow {
                RuntimeException("There is no ${Privilege::class.java.simpleName} with name '$this'")
            }

        private inline fun <reified T: AbstractEntity> T.persist() {
            when (this) {
                is Role -> roleRepository!!.saveAndFlush(this)
                is Privilege -> privilegeRepository!!.saveAndFlush(this)
                else ->
                    throw UnsupportedOperationException("There is no logic (#saveAndFlush) for entity ${T::class.java}")
            }
        }
    }
}
