package org.effective_mobile.task_management_system

import net.datafaker.Faker
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.utils.enums.Priority
import org.effective_mobile.task_management_system.utils.enums.Status

object RandomTasks {
    fun task(user: User,
             executor: User? = null,
             status: Status = Randoms.enum(Status::class.java),
             priority: Priority = Randoms.enum(Priority::class.java)
    ): Task = Task.builder()
            .content(content(30))
            .status(status)
            .priority(priority)
            .creator(user)
            .executor(executor)
            .build()

    private val text = Faker().text()
    fun content(length: Int): String = text.text(length)
    fun priority() = Randoms.enum(Priority::class.java).name
}

