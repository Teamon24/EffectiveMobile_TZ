package org.effective_mobile.task_management_system.component

import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.utils.enums.Status
import org.springframework.stereotype.Component

@Component
class StatusChangeComponent(private val statusChangeValidator: StatusChangeValidator) {

    fun validateStatusChange(customUserDetails: CustomUserDetails, task: Task, newStatus: Status) {
        return statusChangeValidator.validateByRules(customUserDetails, task, newStatus)
    }
}