package org.effective_mobile.task_management_system.component

import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.exception.DeniedOperationException
import org.effective_mobile.task_management_system.exception.messages.AuthorizationExceptionMessages
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.security.authorization.privilege.PrivilegesComponent
import org.effective_mobile.task_management_system.security.authorization.privilege.StatusChangePrivileges
import org.springframework.stereotype.Component

@Component
class StatusChangeValidationComponent(
    private val userComponent: UserComponent,
    private val privilegesComponent: PrivilegesComponent,
    private val statusChangePrivileges: StatusChangePrivileges
) {
    fun isCreatorOrExecutor(details: CustomUserDetails, task: Task) {
        val isNotCreator = !userComponent.isCreator(details, task)
        val isNotExecutor = !userComponent.isExecutor(details, task)

        if (isNotCreator && isNotExecutor) {
            val message = AuthorizationExceptionMessages.neitherCreatorOrExecutor(task.id, details.userId)
            throw DeniedOperationException(message)
        }
    }

    fun isExecutor(customUserDetails: CustomUserDetails, task: Task) {
        userComponent.checkUserIsExecutor(customUserDetails, task)
    }

    fun canStartNewTask(customUserDetails: CustomUserDetails): Boolean {
        val privileges = statusChangePrivileges.canStartNewTask()
        return privilegesComponent.userHasPrivileges(customUserDetails, privileges)
    }

    fun canSuspendTask(customUserDetails: CustomUserDetails): Boolean {
        val privileges = statusChangePrivileges.canSuspendTask()
        return privilegesComponent.userHasPrivileges(customUserDetails, privileges)
    }

    fun canChangeActiveTask(customUserDetails: CustomUserDetails): Boolean {
        val privileges = statusChangePrivileges.canChangeStatusOfActiveTask()
        return privilegesComponent.userHasPrivileges(customUserDetails, privileges)
    }

}
