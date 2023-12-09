package org.effective_mobile.task_management_system.service;

import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.pojo.TaskCreation;
import org.effective_mobile.task_management_system.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private UserService userService;
    private TaskRepository taskRepository;

    @Transactional
    public Long createTask(TaskCreation taskCreation) {
        Task newTask = convert(taskCreation);
        taskRepository.save(newTask);
        return newTask.getId();
    }

    private Task convert(TaskCreation taskCreation) {
        return Task.builder()
            .content(taskCreation.getContent())
            .creator(userService.getByUsername(taskCreation.getUserName()))
            .status(taskCreation.getStatus())
            .priority(taskCreation.getPriority())
            .build();
    }
}
