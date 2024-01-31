package org.effective_mobile.task_management_system.utils

import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.security.CustomUserDetails
import org.effective_mobile.task_management_system.utils.enums.Status
import org.springframework.security.core.GrantedAuthority

typealias Statuses = Collection<Status>
typealias StatusChange = Pair<Status, Status>
typealias StatusChanges = Pair<Status, Statuses>
typealias StatusesChanges = Collection<StatusChange>
typealias UserChecking = (customUserDetails: CustomUserDetails, task: Task) -> Unit
typealias AuthoritiesChecking = (customUserDetails: CustomUserDetails) -> Boolean
typealias AuthoritiesExtracting = (customUserDetails: CustomUserDetails) -> Set<GrantedAuthority>
typealias ExceptionsMap = MutableMap<StatusChange, Exception>

