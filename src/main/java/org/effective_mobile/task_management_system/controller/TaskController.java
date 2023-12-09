package org.effective_mobile.task_management_system.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.effective_mobile.task_management_system.pojo.TaskCreation;
import org.effective_mobile.task_management_system.service.TaskService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController("/task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @RequestMapping(method = RequestMethod.POST, value = "/")
    public Long createTask(
        @Valid @RequestBody @NonNull final TaskCreation taskCreation
    ) {
        return taskService.createTask(taskCreation);
    }
}
