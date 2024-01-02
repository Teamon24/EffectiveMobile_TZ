package org.effective_mobile.task_management_system.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.component.validator.ValidEnum;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.maintain.docs.TaskResourceDocs;
import org.effective_mobile.task_management_system.resource.json.PageResponsePojo;
import org.effective_mobile.task_management_system.resource.json.assignment.AssignmentResponsePojo;
import org.effective_mobile.task_management_system.resource.json.assignment.UnassignmentResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.StatusChangeResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.DeletedTaskResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TasksFiltersRequestPojo;
import org.effective_mobile.task_management_system.service.TaskService;
import org.effective_mobile.task_management_system.utils.converter.TaskConverter;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.effective_mobile.task_management_system.utils.enums.converter.StatusConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.effective_mobile.task_management_system.resource.Api.PathParam.ID;
import static org.effective_mobile.task_management_system.utils.MiscUtils.nullOrApply;

@RestController
@RequestMapping(Api.TASK)
@AllArgsConstructor
public class TaskResource implements TaskResourceDocs {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody TaskResponsePojo createTask(@RequestBody @Valid TaskCreationRequestPojo taskCreationPayload) {
        Task newTask = taskService.createTask(taskCreationPayload);
        return TaskConverter.convertNew(newTask);
    }

    @GetMapping("/{"+ ID + "}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody TaskResponsePojo getTask(@NotNull @PathVariable Long id) {
        Task task = taskService.getTask(id);
        return TaskConverter.convert(task, true);
    }

    @PutMapping("/{"+ ID + "}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody TaskResponsePojo editTask(
        @PathVariable(name = ID) Long id,
        @RequestBody TaskEditionRequestPojo taskEditionRequestPojo
    ) {
        Task editedTask = taskService.editTask(id, taskEditionRequestPojo);
        return TaskConverter.convertEdited(editedTask);
    }

    @DeleteMapping("/{"+ ID + "}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public DeletedTaskResponsePojo deleteTask(@PathVariable(name = ID) Long id) {
        taskService.deleteTask(id);
        return new DeletedTaskResponsePojo(id);
    }

    @PutMapping("/{"+ ID + "}" + Api.EXECUTOR)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody AssignmentResponsePojo setExecutor(
        @PathVariable(name = ID) Long id,
        @RequestParam(Api.QueryParam.EXECUTOR) String newExecutorUsername
    ) {
        User oldExecutor = taskService.setExecutor(id, newExecutorUsername);
        String oldExecutorUsername = nullOrApply(oldExecutor, User::getUsername);
        return new AssignmentResponsePojo(id, newExecutorUsername, oldExecutorUsername);
    }

    @PutMapping("/{"+ ID + "}" + Api.UNASSIGN)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public UnassignmentResponsePojo removeExecutor(@PathVariable(name = ID) Long id) {
        taskService.removeExecutor(id);
        return new UnassignmentResponsePojo(id);
    }

    @PutMapping("/{"+ ID + "}" + Api.STATUS)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody StatusChangeResponsePojo setStatus(
        @PathVariable(name = ID) Long id,
        @RequestParam(name = Api.QueryParam.NEW_STATUS) @ValidEnum(clazz = Status.class) String newStatusStr
    ) {
        Status newStatus = new StatusConverter().convert(newStatusStr);
        Status oldStatus = taskService.setStatus(id, newStatus);
        return new StatusChangeResponsePojo(id, oldStatus, newStatus);
    }

    @GetMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody PageResponsePojo<TaskResponsePojo> getTasks(
        @RequestBody @Valid TasksFiltersRequestPojo tasksFiltersRequestPojo,
        @RequestParam(defaultValue = Api.QueryParam.Page.DEFAULT_INDEX, name = Api.QueryParam.Page.NAME) int pageNumber,
        @RequestParam(defaultValue = Api.QueryParam.Page.DEFAULT_SIZE, name = Api.QueryParam.Page.SIZE) int size
    ) {
        Pageable page = PageRequest.of(pageNumber, size);
        Page<TaskResponsePojo> tasksPage = taskService.getByCreatorOrExecutor(tasksFiltersRequestPojo, page);
        return new PageResponsePojo<>(tasksPage);
    }
}
