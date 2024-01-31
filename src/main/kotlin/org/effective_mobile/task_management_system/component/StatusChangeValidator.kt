package org.effective_mobile.task_management_system.component

import home.extensions.AnysExtensions.invoke
import home.extensions.AnysExtensions.isNull
import home.extensions.BooleansExtensions.invoke
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.exception.DeniedOperationException
import org.effective_mobile.task_management_system.exception.IllegalStatusChangeException
import org.effective_mobile.task_management_system.exception.messages.AuthorizationExceptionMessages
import org.effective_mobile.task_management_system.exception.messages.TaskExceptionMessages
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.utils.AuthoritiesChecking
import org.effective_mobile.task_management_system.utils.ExceptionsMap
import org.effective_mobile.task_management_system.utils.StatusChange
import org.effective_mobile.task_management_system.utils.Statuses
import org.effective_mobile.task_management_system.utils.StatusesChanges
import org.effective_mobile.task_management_system.utils.UserChecking
import org.effective_mobile.task_management_system.utils.enums.Status
import org.effective_mobile.task_management_system.utils.enums.Status.ASSIGNED
import org.effective_mobile.task_management_system.utils.enums.Status.DONE
import org.effective_mobile.task_management_system.utils.enums.Status.EXECUTING
import org.effective_mobile.task_management_system.utils.enums.Status.NEW
import org.effective_mobile.task_management_system.utils.enums.Status.PENDING
import org.springframework.stereotype.Component

@Component
class StatusChangeValidator(private val statusChangeValidationComponent: StatusChangeValidationComponent) {
    private inline fun valid(createRules: ValidStatusChanges.() -> Unit) = ValidStatusChanges().apply(createRules)
    private inline fun invalid(createRules: InvalidStatusChanges.() -> Unit) = InvalidStatusChanges().apply(createRules)

    private val isTaskExecutor          = statusChangeValidationComponent::isExecutor
    private val isTaskCreatorOrExecutor = statusChangeValidationComponent::isCreatorOrExecutor
    private val canStartTask            = statusChangeValidationComponent::canStartNewTask
    private val canSuspendTask          = statusChangeValidationComponent::canSuspendTask
    private val canFinishTask           = statusChangeValidationComponent::canFinishTask
    private val canFinishSuspendedTask  = statusChangeValidationComponent::canFinishSuspendedTask
    private val canResumeTask           = statusChangeValidationComponent::canResumeTask
    private val canResuspendTask        = statusChangeValidationComponent::canResuspendTask

    private val validChanges =
        valid {
            from(ASSIGNED) .to (EXECUTING) user isTaskExecutor          authorized canStartTask
            from(EXECUTING).to (PENDING)   user isTaskCreatorOrExecutor authorized canSuspendTask

            from(EXECUTING).to (DONE)      user isTaskExecutor          authorized canFinishTask
            from(PENDING)  .to (DONE)      user isTaskCreatorOrExecutor authorized canFinishSuspendedTask

            from(PENDING)  .to (EXECUTING) user isTaskExecutor          authorized canResumeTask
            from(DONE)     .to (PENDING)   user isTaskCreatorOrExecutor authorized canResuspendTask
        }

    private val invalidChanges =
        invalid {
            any().to(NEW)      exception IllegalStatusChangeException(TaskExceptionMessages.statusCantBeInitial())
            any().to(ASSIGNED) exception IllegalStatusChangeException(TaskExceptionMessages.statusCantBeAssign())
        }

    private fun any() = Status.values().toList()

    fun validate(
        customUserDetails: CustomUserDetails,
        task: Task,
        newStatus: Status
    ) {
        val currentStatus = task.getStatus()

        invalidChanges {
            val found = exceptionsMap[task.getStatus() to newStatus]
            if (found != null) {
                throw found
            }
        }

        validChanges {
            val statusChange = currentStatus to newStatus

            statusChanges
                .find { (status, possibleStatus) -> statusChange == status to possibleStatus }
                .isNull {
                    throwIllegalStatusChange(task, currentStatus, newStatus)
                }

            userCheckers[statusChange]?.let { userChecker -> userChecker(customUserDetails, task) }
            authoritiesCheckers[statusChange]
                ?.let { authoritiesChecker ->
                    val unauthorized = !authoritiesChecker(customUserDetails)
                    unauthorized {
                        throwDenied(customUserDetails, task, newStatus)
                    }
                }
        }
    }

    private fun throwIllegalStatusChange(task: Task, currentStatus: Status, newStatus: Status) {
        val message = TaskExceptionMessages.impossibleStatusChange(task.id, currentStatus, newStatus)
        throw IllegalStatusChangeException(message)
    }

    private fun throwDenied(customUserDetails: CustomUserDetails,
                            task: Task,
                            newStatus: Status
    ) {
        val message = AuthorizationExceptionMessages.cantChangeStatus(
            customUserDetails.userId,
            task.id,
            task.getStatus(),
            newStatus
        )
        throw DeniedOperationException(message)
    }

    /**
     * Class contains common logic of [ValidStatusChanges] and [InvalidStatusChanges].
     */
    private sealed class ValidatableStatusChanges {
        val statusChanges: MutableSet<StatusChange> = hashSetOf()

        fun from(vararg statuses: Status): Statuses = statuses.toList()

        fun Statuses.to(vararg possibleStatuses: Status): StatusesChanges {
            val result = hashSetOf<StatusChange>()
            possibleStatuses.forEach { possible ->
                forEach { current ->
                    statusChanges.add(current to possible)
                    result.add(current to possible)
                }
            }
            return result
        }
    }

    /**
     * Class contains information about valid status changes.
     */
    private class ValidStatusChanges(
        val userCheckers: MutableMap<StatusChange, UserChecking> = hashMapOf(),
        val authoritiesCheckers: MutableMap<StatusChange, AuthoritiesChecking> = hashMapOf()
    ): ValidatableStatusChanges() {
        infix fun StatusesChanges.user(checking: UserChecking) = onEach { userCheckers[it] = checking }
        infix fun StatusesChanges.authorized(checking: AuthoritiesChecking) = forEach { authoritiesCheckers[it] = checking }
    }

    /**
     * Class that contain information about INVALID status changes and respective exceptions.
     */
    private class InvalidStatusChanges(val exceptionsMap: ExceptionsMap = hashMapOf()): ValidatableStatusChanges() {
        infix fun StatusesChanges.exception(exception: Exception) = forEach { exceptionsMap[it] = exception }
    }
}