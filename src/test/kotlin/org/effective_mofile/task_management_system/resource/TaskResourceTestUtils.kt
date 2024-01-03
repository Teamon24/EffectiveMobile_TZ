package org.effective_mofile.task_management_system.resource

import home.IndicesCartesianProduct
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo
import org.effective_mobile.task_management_system.utils.enums.Priority
import org.effective_mofile.task_management_system.RandomTasks

object TaskResourceTestUtils {
    fun notValidFieldsForTaskCreation(): List<List<Any?>> {
        val taskCreationFields = IndicesCartesianProduct.product(
            arrayListOf(
                arrayListOf(RandomTasks.content(), " ", "", null),
                arrayListOf(RandomTasks.priority(), " ", "", null)
            )
        )

        val notValidFields = taskCreationFields.filter { fields ->
            fields.any { it == null || (it as String).isBlank() }
        }

        return notValidFields
    }

    fun validFieldsForTaskEdition(): List<List<Any?>> {
        return IndicesCartesianProduct.product(
            arrayListOf(
                arrayListOf(RandomTasks.content(), " ", "", null),
                arrayListOf(
                    prioritiesNames().toList() +
                            prioritiesNames().map { it.lowercase() }.toList(),
                    listOf(" "),
                    listOf(""),
                    listOf(null)
                ).flatten(),
            )
        )
    }

    private fun prioritiesNames() = Priority.values().map { it.name }

    fun taskEditing(create: TaskEditionRequestPojo.() -> Unit): TaskEditionRequestPojo {
        return TaskEditionRequestPojo().apply(create)
    }

    fun taskCreation(create: TaskCreationRequestPojo.() -> Unit): TaskCreationRequestPojo {
        return TaskCreationRequestPojo().apply(create)
    }
}