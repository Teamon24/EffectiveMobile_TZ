package org.effective_mobile.task_management_system.pojo

import org.effective_mobile.task_management_system.utils.enums.Priority
import org.effective_mobile.task_management_system.utils.enums.Status
import java.util.function.Function

interface HasTaskInfo:
    HasTaskId,
    HasCreatorUsername, HasExecutorUsername,
    HasStatus, HasPriority,
    HasContent {

    companion object {
        fun getters(): ArrayList<Function<HasTaskInfo, Any?>> {
            return arrayListOf(
                Function { it.getTaskId() },
                Function { it.getContent() },
                Function { it.getStatus() },
                Function { it.getPriority() },
                Function { it.getCreatorUsername() },
                Function { it.getExecutorUsername() }
            )
        }
    }
}

interface HasUserInfo: HasEmail, HasUsername, HasPassword

interface HasContent          { fun getContent()          : String }
interface HasStatus           { fun getStatus()           : Status }
interface HasPriority         { fun getPriority()         : Priority }
interface HasEmail            { fun getEmail()            : String }
interface HasUsername         { fun getUsername()         : String }
interface HasPassword         { fun getPassword()         : String }
interface HasTaskId           { fun getTaskId()           : Long? }
interface HasCreatorUsername  { fun getCreatorUsername()  : String }
interface HasExecutorUsername { fun getExecutorUsername() : String? }


