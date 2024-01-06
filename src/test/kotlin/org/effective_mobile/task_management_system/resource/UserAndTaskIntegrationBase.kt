package org.effective_mobile.task_management_system.resource

import org.effective_mobile.task_management_system.database.entity.AbstractEntity
import org.effective_mobile.task_management_system.database.entity.Task
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.database.repository.TaskRepository
import org.effective_mobile.task_management_system.database.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

abstract class UserAndTaskIntegrationBase: IntegrationTest() {

    @Autowired
    protected lateinit var userRepository: UserRepository
    @Autowired
    protected lateinit var taskRepository: TaskRepository

    internal inline fun <reified T: AbstractEntity> saveAllAndFlush(vararg entities: T) =
        entities.forEach { saveAndFlush(it) }

    @BeforeEach
    fun clearDb() {
        taskRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Transactional
    internal inline fun <reified T> saveAndFlush(entity: T): T {
        return when(entity) {
            is User -> userRepository.saveAndFlush(entity)
            is Task -> taskRepository.saveAndFlush(entity)
            else -> throw UnsupportedOperationException("There is no logic (#saveAndFlush) for class ${T::class.java}")
        }
    }

}
